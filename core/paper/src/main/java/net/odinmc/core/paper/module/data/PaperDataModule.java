package net.odinmc.core.paper.module.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.odinmc.core.common.database.HikariDatabase;
import net.odinmc.core.common.module.data.DataModule;
import net.odinmc.core.common.module.data.PlayerData;
import net.odinmc.core.common.ref.Ref;
import net.odinmc.core.common.scheduling.Promise;
import net.odinmc.core.common.scheduling.Schedulers;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.paper.core.Config;
import net.odinmc.core.paper.events.Events;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PaperDataModule extends DataModule {

    private static final Component ERROR_PLAYER_DATA = Component.text(
        "There was an error loading your player data... Please try again later.",
        NamedTextColor.RED
    );

    private final Ref<Config> config = Services.ref(Config.class);
    private final Map<UUID, PlayerData> playerDataByUUID = new ConcurrentHashMap<>();
    private final Map<Integer, PlayerData> playerDataByID = new ConcurrentHashMap<>();
    private final Cache<UUID, PlayerData> deferredPlayerData = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    @Override
    protected Set<Integer> getPlayerIds() {
        return playerDataByID.keySet();
    }

    @Override
    protected int getPlayerCount() {
        return Bukkit.getServer().getOnlinePlayers().size();
    }

    @Override
    protected int getMaxPlayers() {
        return Bukkit.getMaxPlayers();
    }

    @Override
    protected void setupModule(TerminableConsumer consumer) {
        Events.subscribe(AsyncPlayerPreLoginEvent.class, EventPriority.LOW).handler(this::onAsyncPlayerPreLogin).bindWith(consumer);
        Events.subscribe(AsyncPlayerPreLoginEvent.class, EventPriority.MONITOR).handler(this::onAsyncPlayerPreLoginMonitor).bindWith(consumer);
        Events.subscribe(PlayerLoginEvent.class, EventPriority.LOWEST).handler(this::onPlayerLogin).bindWith(consumer);
        Events.subscribe(PlayerLoginEvent.class, EventPriority.MONITOR).handler(this::onPlayerLoginMonitor).bindWith(consumer);
        Events.subscribe(PlayerJoinEvent.class, EventPriority.LOWEST).handler(this::onPlayerJoin).bindWith(consumer);
        Events.subscribe(PlayerQuitEvent.class, EventPriority.LOWEST).handler(this::onPlayerQuit).bindWith(consumer);
    }

    private void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        var uuid = event.getUniqueId();
        var name = event.getName();
        var address = event.getAddress();
        var serverAddress = Bukkit.getServer().getIp(); // TODO

        try {
            var playerData = playerDataRepository.getByUUID(uuid).join();
            if (playerData == null) {
                playerData = playerDataRepository.create(uuid, name, address, serverAddress).join();
            } else {
                var id = playerData.id();
                var time = System.currentTimeMillis();
                playerData =
                    new PlayerData(
                        playerData.id(),
                        playerData.uuid(),
                        playerData.registerName(),
                        playerData.registerTime(),
                        playerData.registerAddress(),
                        playerData.registerServerAddress(),
                        name,
                        time,
                        address,
                        playerData.lastLogoutTime(),
                        serverAddress
                    );
                playerDataRepository.updateLogin(id, name, time, address, serverAddress);
            }
            deferredPlayerData.put(uuid, playerData);
            var playerLoadEvent = new AsyncPlayerLoadEvent(event, playerData);
            Events.dispatch(playerLoadEvent);

            var result = playerLoadEvent.executeDeferred(databaseExecutor).get();
            if (!result) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ERROR_PLAYER_DATA);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ERROR_PLAYER_DATA);
        }
    }

    private void onAsyncPlayerPreLoginMonitor(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        deferredPlayerData.invalidate(event.getUniqueId());
    }

    private void onPlayerLogin(PlayerLoginEvent event) {
        var player = event.getPlayer();

        //        bukkit.runAsync(() -> profileRepository.save(nmsAdapter.getProfile(player)));

        var playerData = deferredPlayerData.getIfPresent(player.getUniqueId());
        if (playerData == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ERROR_PLAYER_DATA);
            return;
        }
        deferredPlayerData.invalidate(player.getUniqueId());

        playerDataByID.put(playerData.id(), playerData);
        playerDataByUUID.put(playerData.uuid(), playerData);
    }

    private void onPlayerLoginMonitor(PlayerLoginEvent event) {
        var player = event.getPlayer();
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        if (getPlayerData(player) != null) {
            return;
        }
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ERROR_PLAYER_DATA);
    }

    private void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (getPlayerData(player) != null) {
            return;
        }
        player.kick(ERROR_PLAYER_DATA);
    }

    private void onPlayerQuit(PlayerQuitEvent event) {
        var playerData = getPlayerData(event.getPlayer());
        var saveEvent = new AsyncPlayerSaveEvent(event.getPlayer(), playerData);

        Schedulers
            .async()
            .run(() -> {
                Events.dispatch(saveEvent);

                saveEvent
                    .executeDeferred(databaseExecutor)
                    .thenAcceptAsync(ignored -> {
                        playerDataRepository.updateLogout(playerData.id());

                        playerDataByID.remove(playerData.id());
                        playerDataByUUID.remove(playerData.uuid());
                    });
            });
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataByUUID.get(player.getUniqueId());
    }

    @Override
    public Promise<PlayerData> getPlayerData(UUID uuid) {
        var cached = playerDataByUUID.get(uuid);
        if (cached != null) {
            return Promise.completed(cached);
        }
        return super.getPlayerData(uuid);
    }

    @Override
    public Promise<PlayerData> getPlayerData(int id) {
        var cached = playerDataByID.get(id);
        if (cached != null) {
            return Promise.completed(cached);
        }
        return super.getPlayerData(id);
    }

    @Override
    protected void populateDatabases() {
        var config = this.config.get();
        for (var entry : config.getModules().getData().getDatabases().entrySet()) {
            var name = entry.getKey();
            var info = entry.getValue();
            databases.put(name, new HikariDatabase(info.getUrl(), info.getUsername(), info.getPassword(), databaseExecutor));
        }
    }
}
