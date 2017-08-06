package com.cracknellj.fare.ws.dao;

import java.sql.*;

public abstract class AbstractDAO {
    private static final String DB_USER = "admin";
    private static final String DB_PATH = "jdbc:mysql://localhost:3306/tfl";
    private static final String DB_PASSWORD = "admin";

    Connection cn;
    PreparedStatement ps;
    ResultSet rs;

    void connectToDatabase() throws SQLException {
        if (cn == null || cn.isClosed()) {
            cn = DriverManager.getConnection(DB_PATH, DB_USER, DB_PASSWORD);
        }
    }

    void closeConnection() {
        for (AutoCloseable closeable : new AutoCloseable[]{rs, ps, cn}) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
