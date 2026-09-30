package com.hostel.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if users table exists and has data
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'users'");
            if (rs.next()) {
                ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (countRs.next() && countRs.getInt(1) > 0) {
                    // Database is already populated
                    return;
                }
            }

            System.out.println("Initializing database schema and seed data...");
            runSqlFile(conn, "database/schema.sql");
            runSqlFile(conn, "database/seed.sql");
            System.out.println("Database initialization completed successfully.");

        } catch (Exception e) {
            System.err.println("Database auto-initialization notice: " + e.getMessage());
        }
    }

    private static void runSqlFile(Connection conn, String relativePath) {
        try {
            String sqlContent;
            if (Files.exists(Paths.get(relativePath))) {
                sqlContent = Files.readString(Paths.get(relativePath), StandardCharsets.UTF_8);
            } else {
                InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream(relativePath);
                if (is == null) return;
                sqlContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            String[] statements = sqlContent.split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--") && !trimmed.startsWith("/*")) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing " + relativePath + ": " + e.getMessage());
        }
    }
}
