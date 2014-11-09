package org.jperf.jdbc;

import org.jperf.PerfTest;
import org.jperf.PerfTestFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcPerfTestFactory implements PerfTestFactory {

  private String jdbcDriverClassName;
  private String jdbcURL;
  private String jdbcUser;
  private String jdbcPass;
  private String sql;

  public JdbcPerfTestFactory(String jdbcDriverClassName, String jdbcURL, String jdbcUser, String jdbcPass, String sql) throws Exception {
    this.jdbcDriverClassName = jdbcDriverClassName;
    this.jdbcURL = jdbcURL;
    this.jdbcUser = jdbcUser;
    this.jdbcPass = jdbcPass;
    this.sql = sql;

    Class.forName(jdbcDriverClassName);
  }

  @Override
  public PerfTest createPerfTest() throws Exception {
    Connection conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPass);
    conn.setAutoCommit(true);
    return new JdbcPerfTest(conn, sql);
  }
}
