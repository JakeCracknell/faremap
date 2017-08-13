package com.cracknellj.fare.dao;

import java.sql.*;

public abstract class AbstractDAO {
    private static final String DB_USER = "admin";
    private static final String DB_PATH = "jdbc:mysql://localhost:3306/tfl?autoReconnect=true&useSSL=false";
    private static final String DB_PASSWORD = "admin";

    protected Connection cn;
    protected PreparedStatement ps;
    protected ResultSet rs;

    protected void connectToDatabase() throws SQLException {
        if (cn == null || cn.isClosed()) {
            cn = DriverManager.getConnection(DB_PATH, DB_USER, DB_PASSWORD);
        }
    }

    protected void closeConnection() {
        for (AutoCloseable closeable : new AutoCloseable[]{rs, ps, cn}) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
