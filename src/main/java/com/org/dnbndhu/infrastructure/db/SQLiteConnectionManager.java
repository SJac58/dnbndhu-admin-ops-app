package com.org.dnbndhu.infrastructure.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnectionManager {

    private static final String DB_URL =
            "jdbc:sqlite:data/deenabandhu.db";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);

            // IMPORTANT: Enable foreign keys per connection
            Statement stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");

            return conn;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite DB", e);
        }
    }
}
