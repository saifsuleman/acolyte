package net.odinmc.core.common.database;

import com.google.common.base.Throwables;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executor;
import net.odinmc.core.common.scheduling.Promise;

@SuppressWarnings({ "deprecation", "SqlSourceToSinkFlow" })
public class AsyncDatabaseImpl implements AsyncDatabase {

    private final Database database;
    private final Executor executor;

    public AsyncDatabaseImpl(Database database, Executor executor) {
        this.database = database;
        this.executor = executor;
    }

    @Override
    public Promise<Void> execute(String sql, DatabasePreparer preparer) {
        Promise<Void> promise = Promise.empty();

        executor.execute(() -> {
            try {
                try (var connection = database.getConnection()) {
                    try (var statement = connection.prepareStatement(sql)) {
                        if (preparer != null) {
                            preparer.prepare(statement);
                        }
                        statement.execute();
                        promise.complete(null);
                    }
                }
            } catch (SQLException exception) {
                throw Throwables.propagate(exception);
            }
        });

        return promise;
    }

    @Override
    public Promise<Integer> executeUpdate(String sql, DatabasePreparer preparer) {
        Promise<Integer> promise = Promise.empty();
        executor.execute(() -> {
            try {
                try (var connection = database.getConnection()) {
                    try (var statement = connection.prepareStatement(sql)) {
                        if (preparer != null) {
                            preparer.prepare(statement);
                        }
                        var result = statement.executeUpdate();
                        promise.complete(result);
                    }
                }
            } catch (SQLException exception) {
                throw Throwables.propagate(exception);
            }
        });
        return promise;
    }

    @Override
    public Promise<int[]> executeBatch(String sql, DatabasePreparer preparer) {
        Promise<int[]> promise = Promise.empty();
        executor.execute(() -> {
            try {
                try (var connection = database.getConnection()) {
                    try (var statement = connection.prepareStatement(sql)) {
                        if (preparer != null) {
                            preparer.prepare(statement);
                        }
                        var results = statement.executeBatch();
                        promise.complete(results);
                    }
                }
            } catch (SQLException exception) {
                throw Throwables.propagate(exception);
            }
        });
        return promise;
    }

    @Override
    public Promise<ResultSet> executeGeneratedKeys(String sql, DatabasePreparer preparer) {
        Promise<ResultSet> promise = Promise.empty();
        executor.execute(() -> {
            try {
                try (var connection = database.getConnection()) {
                    try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        if (preparer != null) {
                            preparer.prepare(statement);
                        }
                        statement.execute();
                        promise.complete(statement.getGeneratedKeys());
                    }
                }
            } catch (SQLException exception) {
                throw Throwables.propagate(exception);
            }
        });
        return promise;
    }

    @Override
    public Promise<ResultSet> executeQuery(String sql, DatabasePreparer preparer) {
        Promise<ResultSet> promise = Promise.empty();
        executor.execute(() -> {
            try {
                try (var connection = database.getConnection()) {
                    try (var statement = connection.prepareStatement(sql)) {
                        if (preparer != null) {
                            preparer.prepare(statement);
                        }
                        var results = statement.executeQuery();
                        promise.complete(results);
                    }
                }
            } catch (SQLException exception) {
                throw Throwables.propagate(exception);
            }
        });
        return promise;
    }
}
