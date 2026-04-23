package com.org.dnbndhu.infrastructure.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class SQLiteConnectionManager {

    private static final String DB_URL =
            "jdbc:sqlite:" + System.getProperty("user.dir") + "/data/deenabandhu.db";

    static {
        try {
            // Explicit driver loading (important when packaging as .exe)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC Driver not found.", e);
        }
    }

    private SQLiteConnectionManager() {
        // Prevent instantiation
    }

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);

            // Enable foreign key support (must be done per connection in SQLite)
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            return connection;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite database.", e);
        }
    }
}
