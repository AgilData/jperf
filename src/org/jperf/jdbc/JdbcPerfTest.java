package org.jperf.jdbc;

import org.jperf.PerfTest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcPerfTest implements PerfTest {

    private Connection conn;

    private String sql;

    public JdbcPerfTest(Connection conn, String sql) {
        this.conn = conn;
        this.sql = sql;
    }

    @Override
    public void setUp() throws Exception {
    }

    @Override
    public void test() throws Exception {
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        ResultSet rs = stmt.getResultSet();
        if (rs!=null) {
            // read the first column from each row in the result set
            while (rs.next()) {
                rs.getObject(1);
            }
            rs.close();
        }
        stmt.close();
    }

    @Override
    public void tearDown() throws Exception {
        conn.close();
    }
}
