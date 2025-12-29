package com.org.dnbndhu.infrastructure.db;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class SchemaInitializer {

    public static void init() {

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            String schemaSql = Files.readString(
                    Paths.get("src/main/resources/db/schema.sql")
            );

            stmt.executeUpdate(schemaSql);

            System.out.println("✔ Database schema initialized");

        } catch (Exception e) {
            throw new RuntimeException("Schema initialization failed", e);
        }
    }
}

