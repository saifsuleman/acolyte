package net.odinmc.core.common.database;

import net.odinmc.core.common.scheduling.Promise;

import java.sql.ResultSet;
import java.util.Collection;

public interface AsyncDatabase {

  default Promise<Void> execute(String sql) {
    return execute(sql, null);
  }

  Promise<Void> execute(String sql, DatabasePreparer preparer);

  default Promise<Integer> executeUpdate(String sql) {
    return executeUpdate(sql, null);
  }

  Promise<Integer> executeUpdate(String sql, DatabasePreparer preparer);

  Promise<int[]> executeBatch(String sql, DatabasePreparer preparer);

  Promise<ResultSet> executeGeneratedKeys(String sql, DatabasePreparer preparer);

  default Promise<ResultSet> executeGeneratedKeys(String sql) {
    return executeGeneratedKeys(sql, null);
  }

  default Promise<ResultSet> executeQuery(String sql) {
    return executeQuery(sql, null);
  }

  Promise<ResultSet> executeQuery(String sql, DatabasePreparer preparer);
}
