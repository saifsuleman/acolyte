package net.odinmc.core.common.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public interface DatabasePreparer {
    void prepare(PreparedStatement statement) throws SQLException;

    static DatabasePreparer prepareCollection(Collection<Object> objects) {
        return statement -> {
            int index = 0;

            for (var o : objects) {
                statement.setObject(++index, o);
            }
        };
    }
}
