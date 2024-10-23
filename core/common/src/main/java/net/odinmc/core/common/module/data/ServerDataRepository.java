package net.odinmc.core.common.module.data;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.odinmc.core.common.database.Database;
import net.odinmc.core.common.scheduling.Promise;
import net.odinmc.core.common.services.Services;

public class ServerDataRepository {

    private final Database database = Services.load(Database.class);

    public Promise<ServerData> create(String name, int players, int maxPlayers) {
        long time = System.currentTimeMillis();
        return database
            .executeGeneratedKeys(
                "INSERT INTO servers (name, players, max_players, last_update_time) VALUES (?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, name);
                    statement.setInt(2, players);
                    statement.setInt(3, maxPlayers);
                    statement.setTimestamp(4, new Timestamp(time));
                }
            )
            .thenApplyAsync(result -> {
                try {
                    result.next();
                    return new ServerData(result.getInt(1), name, players, maxPlayers, time);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public Promise<Integer> getIdByName(String name) {
        return database
            .executeQuery("SELECT id FROM servers WHERE name = ?", statement -> statement.setString(1, name))
            .thenApplyAsync(result -> {
                try {
                    if (result.next()) {
                        return result.getInt(1);
                    }
                    return 0;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public Promise<List<ServerData>> getByNameLike(String like) {
        return database
            .executeQuery(
                "SELECT id, name, players, max_players, last_update_time FROM servers WHERE name LIKE ? AND last_update_time > DATE_SUB(NOW(), INTERVAL 5 SECOND)",
                statement -> statement.setString(1, like)
            )
            .thenApplyAsync(result -> {
                List<ServerData> servers = new ArrayList<>();
                try {
                    while (result.next()) {
                        servers.add(
                            new ServerData(
                                result.getInt(1),
                                result.getString(2),
                                result.getInt(3),
                                result.getInt(4),
                                result.getTimestamp(5).getTime()
                            )
                        );
                    }
                    return servers;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public Promise<Boolean> updateStatus(int id, int players, int maxPlayers) {
        return database
            .executeUpdate(
                "UPDATE servers SET players = ?, max_players = ?, last_update_time = NOW() WHERE id = ?",
                statement -> {
                    statement.setInt(1, players);
                    statement.setInt(2, maxPlayers);
                    statement.setInt(3, id);
                }
            )
            .thenApplyAsync(result -> result > 0);
    }

    public Promise<Void> createPlayers(int id, Collection<Integer> playerIds) {
        return database.execute(
            "INSERT INTO players_to_server (player_id, server_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE server_id = VALUES(server_id)",
            statement -> {
                for (var playerId : playerIds) {
                    statement.setInt(1, playerId);
                    statement.setInt(2, id);
                    statement.addBatch();
                }
            }
        );
    }

    public Promise<Void> deletePlayersById(int id, Collection<Integer> playerIds) {
        return database.execute(
            "DELETE FROM players_to_server WHERE player_id = ? AND server_id = ?",
            statement -> {
                for (var playerId : playerIds) {
                    statement.setInt(1, playerId);
                    statement.setInt(2, id);
                    statement.addBatch();
                }
            }
        );
    }

    public Promise<Void> deleteAllPlayersByIdExcluding(int id, Collection<Integer> playerIds) {
        String placeholders = String.join(", ", Collections.nCopies(playerIds.size(), "?"));
        return database.execute(
            "DELETE FROM players_to_server WHERE server_id = ? AND player_id NOT IN (" + placeholders + ")",
            statement -> {
                statement.setInt(1, id);
                int i = 2;
                for (var playerId : playerIds) {
                    statement.setInt(i++, playerId);
                }
            }
        );
    }

    public Promise<Void> deleteAllPlayersById(int id) {
        return database.execute("DELETE FROM players_to_server WHERE server_id = ?", statement -> statement.setInt(1, id));
    }
}
