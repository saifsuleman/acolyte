package net.odinmc.core.common.module.level;

import java.sql.SQLException;
import net.odinmc.core.common.database.Database;
import net.odinmc.core.common.scheduling.Promise;
import net.odinmc.core.common.services.Services;

public class LevelRepository {

    private final Database database = Services.load(Database.class);

    public Promise<String> getResourcesByName(String level) {
        return database
            .executeQuery(
                "SELECT resources FROM levels WHERE name = ?",
                statement -> {
                    statement.setString(1, level);
                }
            )
            .thenApplyAsync(result -> {
                try {
                    if (result.next()) {
                        return result.getString(1);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return null;
            });
    }

    public Promise<Integer> setResources(String level, String resources) {
        return database.executeUpdate(
            "INSERT INTO levels (name, resources) VALUES (?, ?) ON DUPLICATE KEY UPDATE resources = VALUES(resources)",
            statement -> {
                statement.setString(1, level);
                statement.setString(2, resources);
            }
        );
    }
}
