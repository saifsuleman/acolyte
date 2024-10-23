package net.odinmc.core.common.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;

public interface Database extends AsyncDatabase {

  default AsyncDatabase sync() {
    return async(Runnable::run);
  }

  AsyncDatabase async();

  AsyncDatabase async(Executor executor);

  Connection getConnection() throws SQLException;

  String getURL();

  String getUsername();

  String getPassword();

  Executor getExecutor();
}
