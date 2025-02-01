package org.example.migration;

import lombok.Generated;
import migrations.MigrationScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MigrationManager {

    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class);

    private static final String MIGRATION_TABLE = "migrations";
    private static final String MIGRATION_DIR = "src/main/resources/migrations";
    private static final String ORDER_FILE = "src/main/resources/sql-order.txt";
    private static final String CHECKSUM_ALGORITHM = "SHA-256";

    private final MigrationExecutor executor;
    private final Connection connection;

    public MigrationManager() throws SQLException {
        this.executor = new MigrationExecutor();
        this.connection = DatabaseConnection.getConnection();
    }

    public void runMigrations() {

        try {
            createMigrationTableIfNotExists();

            List<String> migrationOrder = readMigrationOrder();
            List<File> migrationFiles = getMigrationFiles();

            for (String orderedFileName : migrationOrder) {
                File migrationFile = migrationFiles.stream()
                        .filter(file -> file.getName().equals(orderedFileName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Migration file not found: " + orderedFileName));

                String version = extractVersion(migrationFile);
                String checksum = calculateChecksum(migrationFile);

                if (!isMigrationApplied(version, checksum)) {
                    logger.info("Applying migration: version {}, file {}", version, migrationFile.getName());
                    applyMigration(migrationFile);
                    markMigrationAsApplied(version, checksum);
                    logger.info("Migration applied successfully: version {}", version);
                } else {
                    logger.info("Migration already applied: version {}, file {}", version, migrationFile.getName());
                }
            }
        } catch (SQLException | IOException | NoSuchAlgorithmException e) {
            logger.error("Error during migration process: {}", e.getMessage(), e);
        }
    }

    @Generated
    public void rollbackToDate(LocalDateTime rollbackDate) {
        try {
            createMigrationTableIfNotExists();

            String query = "SELECT version, checksum FROM " + MIGRATION_TABLE + " WHERE applied_at > ? ORDER BY applied_at DESC";
            List<String> migrationsToRollback = new ArrayList<>();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setTimestamp(1, Timestamp.valueOf(rollbackDate));
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String version = rs.getString("version");
                        migrationsToRollback.add(version);
                    }
                }
            }

            if (migrationsToRollback.isEmpty()) {
                logger.info("No migrations to rollback after {}", rollbackDate);
                return;
            }

            logger.info("Found {} migrations to rollback.", migrationsToRollback.size());

            for (String version : migrationsToRollback) {
                String rollbackScriptName = "R" + version + "__rollback.sql";
                File rollbackFile = new File(MIGRATION_DIR, rollbackScriptName);

                if (!rollbackFile.exists()) {
                    logger.warn("Rollback script not found for version {}: {}", version, rollbackScriptName);
                    continue;
                }

                logger.info("Rolling back migration version {} using file {}", version, rollbackFile.getName());
                String rollbackScript = readSqlFromFile(rollbackFile);
                executor.executeMigration(rollbackScript);

                removeMigrationRecord(version);
                logger.info("Rolled back migration version {} successfully.", version);
            }
        } catch (SQLException e) {
            logger.error("Error during rollback: {}", e.getMessage(), e);
        }
    }

    @Generated
    public void removeMigrationRecord(String version) throws SQLException {
        String sql = "DELETE FROM " + MIGRATION_TABLE + " WHERE version = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, version);
            stmt.executeUpdate();
        }
    }

    public void createMigrationTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + MIGRATION_TABLE + " (" +
                "id SERIAL PRIMARY KEY, " +
                "version VARCHAR(50) NOT NULL, " +
                "checksum VARCHAR(64) NOT NULL, " +
                "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            logger.info("Migration table created or already exists.");
        } catch (SQLException e) {
            logger.error("Failed to create migration table: {}", e.getMessage(), e);
            throw e;
        }
        String createTableQuery = "CREATE TABLE IF NOT EXISTS migration_lock (" +
                "id INT PRIMARY KEY, " +
                "is_locked BOOLEAN)";
        try (PreparedStatement createTableStmt = connection.prepareStatement(createTableQuery)) {
            createTableStmt.executeUpdate();
        }
        String insertRow = "INSERT INTO migration_lock (id, is_locked) VALUES (1, FALSE) ON CONFLICT (id) DO NOTHING";
        try (PreparedStatement createTableStmt = connection.prepareStatement(insertRow)) {
            createTableStmt.executeUpdate();
        }
    }


    public List<String> readMigrationOrder() throws IOException {
        File orderFile = new File(ORDER_FILE);
        if (!orderFile.exists()) {
            logger.error("Order file does not exist: {}", ORDER_FILE);
            throw new IllegalArgumentException("Order file does not exist: " + ORDER_FILE);
        }

        return java.nio.file.Files.lines(orderFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.toList());
    }

    @Generated
    public List<File> getMigrationFiles() {
        File migrationDir = new File(MIGRATION_DIR);
        if (!migrationDir.exists() || !migrationDir.isDirectory()) {
            logger.error("Migration directory does not exist: {}", MIGRATION_DIR);
            throw new IllegalArgumentException("Migration directory does not exist: " + MIGRATION_DIR);
        }

        List<File> migrationFiles = Arrays.stream(migrationDir.listFiles())
                .filter(file -> file.getName().endsWith(".sql"))
                .collect(Collectors.toList());

        logger.info("Found {} migration files.", migrationFiles.size());
        return migrationFiles;
    }

    @Generated
    public String extractVersion(File file) {
        String filename = file.getName();
        String version = filename.split("__")[0].replaceAll("[^0-9]", "");
        return version;
    }

    @Generated
    public String calculateChecksum(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(CHECKSUM_ALGORITHM);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String checksum = hexString.toString();
        return checksum;
    }

    public boolean isMigrationApplied(String version, String checksum) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + MIGRATION_TABLE + " WHERE version = ? AND checksum = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, version);
            stmt.setString(2, checksum);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            boolean isApplied = rs.getInt(1) > 0;
            return isApplied;
        }
    }

    @Generated
    public void applyMigration(File file) throws SQLException {
        String sql = readSqlFromFile(file);
        executor.executeMigration(sql);
        logger.info("Executed migration from file: {}", file.getName());
    }

    @Generated
    public void markMigrationAsApplied(String version, String checksum) throws SQLException {
        String sql = "INSERT INTO " + MIGRATION_TABLE + " (version, checksum) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, version);
            stmt.setString(2, checksum);
            stmt.executeUpdate();
            logger.info("Marked migration as applied: version {}", version);
        }
    }

    @Generated
    public String readSqlFromFile(File file) {
        try {
            String sql = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            return sql;
        } catch (IOException e) {
            logger.error("Failed to read SQL file: {}", file.getName(), e);
            throw new RuntimeException("Failed to read SQL file: " + file.getName(), e);
        }
    }
}
