package net.odinmc.core.common.module.data;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.odinmc.core.common.database.Database;
import net.odinmc.core.common.network.Network;
import net.odinmc.core.common.scheduling.Promise;
import net.odinmc.core.common.scheduling.Schedulers;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.module.TerminableModule;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public abstract class DataModule implements TerminableModule {
    public static final String DEFAULT_DATABASE = "default";
    private static final long FULL_PLAYER_UPDATE_INTERVAL = 60000L;

    protected final Network network = Services.load(Network.class);
    protected final ExecutorService databaseExecutor = new ThreadPoolExecutor(
            10, 100,
            1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("odin-database-executor-%d").build());
    protected final Map<String, Database> databases = new HashMap<>();
    private final ReentrantLock playerUpdateLock = new ReentrantLock();
    protected PlayerDataRepository playerDataRepository;
    protected ServerDataRepository serverDataRepository;
    private int serverId;
    private Set<Integer> lastUpdatePlayerIds = new HashSet<>();
    private long lastFullPlayerUpdate = 0;

    @Override
    public void setup(TerminableConsumer consumer) {
        populateDatabases();

        var database = databases.get(DEFAULT_DATABASE);
        Services.provide(Database.class, database);
        Services.provideApi(this, DataModule.class);

        playerDataRepository = Services.getOrProvide(PlayerDataRepository.class);
        serverDataRepository = Services.getOrProvide(ServerDataRepository.class);

        try {
            serverId = serverDataRepository.getIdByName(network.getServerName()).get();
            if (serverId == 0) {
                var serverData = serverDataRepository.create(network.getServerName(), getPlayerCount(), getMaxPlayers());
                serverId = serverData.get().id();
                if (serverId == 0) {
                    throw new IllegalStateException();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        Schedulers.async().scheduleRepeating(() -> {
            updateServer();
            updatePlayers();
        }, Duration.ofSeconds(1));
    }

    private void updateServer() {
        serverDataRepository.updateStatus(serverId, getPlayerCount(), getMaxPlayers());
    }

    private void updatePlayers() {
        if (!playerUpdateLock.tryLock()) {
            return;
        }
        try {
            var playerIds = getPlayerIds();
            if (System.currentTimeMillis() - lastFullPlayerUpdate > FULL_PLAYER_UPDATE_INTERVAL) {
                if (playerIds.isEmpty()) {
                    serverDataRepository.deleteAllPlayersById(serverId);
                } else {
                    serverDataRepository.createPlayers(serverId, playerIds);
                    serverDataRepository.deleteAllPlayersByIdExcluding(serverId, playerIds);
                }
                lastFullPlayerUpdate = System.currentTimeMillis();
            } else {
                var playerIdsToInsert = new HashSet<>(playerIds);
                playerIdsToInsert.removeAll(lastUpdatePlayerIds);
                var playerIdsToDelete = new HashSet<>(lastUpdatePlayerIds);
                playerIdsToDelete.removeAll(playerIds);
                if (!playerIdsToInsert.isEmpty()) {
                    serverDataRepository.createPlayers(serverId, playerIdsToInsert);
                }
                if (!playerIdsToDelete.isEmpty()) {
                    serverDataRepository.deletePlayersById(serverId, playerIdsToDelete);
                }
            }
            this.lastUpdatePlayerIds = playerIds;
        } finally {
            playerUpdateLock.unlock();
        }
    }

    public ServerGroupData getServerGroup(String like) {
        return new ServerGroupData(this, like);
    }

    public Promise<PlayerData> getPlayerData(int id) {
        return playerDataRepository.getById(id);
    }

    public Promise<PlayerData> getPlayerData(UUID uuid) {
        return playerDataRepository.getByUUID(uuid);
    }

    public Promise<PlayerData> getPlayerData(String name) {
        return playerDataRepository.getByLastLoginName(name);
    }

    public Promise<List<ServerData>> getServersLike(String like) {
        return serverDataRepository.getByNameLike(like);

    }

    public Database database() {
        return database(DEFAULT_DATABASE);
    }

    public Database database(String id) {
        return databases.get(id);
    }

    public ExecutorService databaseExecutor() {
        return databaseExecutor;
    }

    public int serverId() {
        return serverId;
    }

    protected abstract Set<Integer> getPlayerIds();

    protected abstract int getPlayerCount();

    protected abstract int getMaxPlayers();

    protected abstract void setupModule(TerminableConsumer consumer);

    protected abstract void populateDatabases();
}
