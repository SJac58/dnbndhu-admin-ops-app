package com.org.dnbndhu.infrastructure.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public final class SchemaInitializer {

    private SchemaInitializer() {
    }

    public static void init() {

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                Statement stmt = conn.createStatement()
        ) {

            InputStream inputStream = SchemaInitializer.class
                    .getClassLoader()
                    .getResourceAsStream("db/schema.sql");

            if (inputStream == null) {
                throw new RuntimeException("schema.sql not found in resources/db/");
            }

            String schemaSql = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            stmt.executeUpdate(schemaSql);

            System.out.println("✔ Database schema initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Schema initialization failed", e);
        }
    }
}
