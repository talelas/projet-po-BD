package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private Db() {}

    public static Connection getConnection() throws SQLException {
        // MySQL JDBC drivers usually auto-register, but this makes failures clearer.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // If the jar isn't on the classpath, DriverManager will fail below.
        }
        return DriverManager.getConnection(DbConfig.jdbcUrl(), DbConfig.USER, DbConfig.PASSWORD);
    }
}
