package ui.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection management
 * Uses direct JDBC connection (no pooling needed for single-user desktop app)
 */
public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacydb1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "140406";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }
    
    /**
     * Get a direct database connection
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Close connection pool (no-op for direct connections)
     */
    public static void closePool() {
        // Not needed for direct JDBC connections
    }
}
