package net.odinmc.core.common.module.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import net.odinmc.core.common.database.Database;
import net.odinmc.core.common.scheduling.Promise;
import net.odinmc.core.common.services.Services;

public class PlayerDataRepository {

    private final Database database = Services.load(Database.class);

    public Promise<PlayerData> create(UUID uuid, String name, InetAddress address, String serverAddress) {
        var time = System.currentTimeMillis();
        return database
            .executeGeneratedKeys(
                "INSERT INTO players (uuid_upper_bits, uuid_lower_bits, register_name, register_time, register_address," +
                " register_server_address, last_login_name, last_login_time, last_login_address, last_server_address)" +
                " VALUES (?, ?, ?, ?, INET_ATON(?), VALUES(register_name), VALUES(register_time), VALUES(register_address), VALUES(register_server_address))",
                statement -> {
                    statement.setLong(1, uuid.getMostSignificantBits());
                    statement.setLong(2, uuid.getLeastSignificantBits());
                    statement.setString(3, name);
                    statement.setTimestamp(4, new Timestamp(time));
                    statement.setString(5, serverAddress);
                }
            )
            .thenApplyAsync(result -> {
                try {
                    result.next();
                    return new PlayerData(result.getInt(1), uuid, name, time, address, serverAddress, name, time, address, null, serverAddress);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public Promise<PlayerData> getById(int id) {
        return database
            .executeQuery(
                "SELECT uuid_upper_bits, uuid_lower_bits, register_name, register_time, INET_NTOA(register_address), " +
                "register_server_address, last_login_name, last_login_time, INET_NTOA(last_login_address), last_logout_time " +
                "FROM players WHERE id = ?",
                statement -> {
                    statement.setInt(1, id);
                }
            )
            .thenApplyAsync(result -> {
                try {
                    if (result.next()) {
                        var lastLogoutTime = result.getTimestamp(10);
                        UUID uuid = new UUID(result.getLong(1), result.getLong(2));
                        return new PlayerData(
                            id,
                            uuid,
                            result.getString(3),
                            result.getTimestamp(4).getTime(),
                            InetAddress.getByName(result.getString(5)),
                            result.getString(6),
                            result.getString(7),
                            result.getTimestamp(8).getTime(),
                            InetAddress.getByName(result.getString(9)),
                            lastLogoutTime != null ? lastLogoutTime.getTime() : null,
                            result.getString(10)
                        );
                    }
                    return null;
                } catch (SQLException | UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public Promise<PlayerData> getByUUID(UUID uuid) {
        return database
            .executeQuery(
                "SELECT id, register_name, register_time, INET_NTOA(register_address), register_server_address, " +
                "last_login_name, last_login_time, INET_NTOA(last_login_address), last_login_server_address, last_logout_time " +
                "FROM players WHERE uuid_upper_bits = ? AND uuid_lower_bits = ?",
                statement -> {
                    statement.setLong(1, uuid.getMostSignificantBits());
                    statement.setLong(2, uuid.getLeastSignificantBits());
                }
            )
            .thenApplyAsync(result -> {
                try {
                    if (result.next()) {
                        var lastLogoutTime = result.getTimestamp(10);
                        return new PlayerData(
                            result.getInt(1),
                            uuid,
                            result.getString(2),
                            result.getTimestamp(3).getTime(),
                            InetAddress.getByName(result.getString(4)),
                            result.getString(5),
                            result.getString(6),
                            result.getTimestamp(7).getTime(),
                            InetAddress.getByName(result.getString(8)),
                            lastLogoutTime != null ? lastLogoutTime.getTime() : null,
                            result.getString(9)
                        );
                    }
                    return null;
                } catch (SQLException | UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public Promise<PlayerData> getByLastLoginName(String name) {
        return database
            .executeQuery(
                "SELECT id, uuid_upper_bits, uuid_lower_bits, register_name, register_time, INET_NTOA(register_address), " +
                "register_server_address, last_login_time, INET_NTOA(last_login_address), last_login_server_address, last_logout_time " +
                "FROM players WHERE last_login_name = ? ORDER BY last_login_time DESC LIMIT 1",
                statement -> {
                    statement.setString(1, name);
                }
            )
            .thenApplyAsync(result -> {
                try {
                    if (result.next()) {
                        var lastLogoutTime = result.getTimestamp(10);
                        UUID uuid = new UUID(result.getLong(2), result.getLong(3));
                        return new PlayerData(
                            result.getInt(1),
                            uuid,
                            result.getString(4),
                            result.getTimestamp(5).getTime(),
                            InetAddress.getByName(result.getString(6)),
                            result.getString(7),
                            name,
                            result.getTimestamp(8).getTime(),
                            InetAddress.getByName(result.getString(9)),
                            lastLogoutTime != null ? lastLogoutTime.getTime() : null,
                            result.getString(10)
                        );
                    }
                    return null;
                } catch (SQLException | UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public Promise<Boolean> updateLogin(int id, String name, long lastLoginTime, InetAddress address, String serverAddress) {
        return database
            .executeUpdate(
                "UPDATE players SET last_login_name = ?, last_login_time = ?, last_login_address = INET_ATON(?), " +
                "last_login_server_address = ? WHERE id = ?",
                statement -> {
                    statement.setString(1, name);
                    statement.setTimestamp(2, new Timestamp(lastLoginTime));
                    statement.setString(3, address.getHostAddress());
                    statement.setString(4, serverAddress);
                    statement.setInt(5, id);
                }
            )
            .thenApplyAsync(result -> result > 0);
    }

    public Promise<Boolean> updateLogout(int id) {
        return database
            .executeUpdate(
                "UPDATE players SET last_logout_time = NOW() WHERE id = ?",
                statement -> {
                    statement.setInt(1, id);
                }
            )
            .thenApplyAsync(result -> result > 0);
    }
}
