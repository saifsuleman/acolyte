package net.odinmc.core.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.odinmc.core.common.scheduling.Promise;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executor;

public class HikariDatabase implements Database, AsyncDatabase {

  private final String url;
  private final String username;
  private final String password;
  private final HikariDataSource dataSource;
  private final AsyncDatabaseImpl async;
  private final Executor executor;

  public HikariDatabase(String url, String username, String password, Executor executor) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.executor = executor;

    var config = new HikariConfig();
    config.setJdbcUrl(this.url);
    config.setUsername(this.username);
    config.setPassword(this.password);
    config.setMaximumPoolSize(20);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    this.dataSource = new HikariDataSource(config);

    this.async = new AsyncDatabaseImpl(this, executor);
  }

  @Override
  public AsyncDatabase async() {
    return async;
  }

  @Override
  public AsyncDatabase async(Executor executor) {
    return new AsyncDatabaseImpl(this, executor);
  }

  @Override
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public String getURL() {
    return url;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public Executor getExecutor() {
    return executor;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Promise<Void> execute(String sql, DatabasePreparer preparer) {
    return async.execute(sql, preparer);
  }

  @Override
  public Promise<Integer> executeUpdate(String sql, DatabasePreparer preparer) {
    return async.executeUpdate(sql, preparer);
  }

  @Override
  public Promise<int[]> executeBatch(String sql, DatabasePreparer preparer) {
    return async.executeBatch(sql, preparer);
  }

  @Override
  public Promise<ResultSet> executeGeneratedKeys(String sql, DatabasePreparer preparer) {
    return async.executeGeneratedKeys(sql, preparer);
  }

  @Override
  public Promise<ResultSet> executeQuery(String sql, DatabasePreparer preparer) {
    return async.executeQuery(sql, preparer);
  }
}
