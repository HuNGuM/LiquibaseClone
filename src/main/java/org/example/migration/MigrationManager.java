package org.example.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MigrationManager {

    private static final Logger logger = LoggerFactory.getLogger(MigrationManager.class); // Logger initialization

    private static final String MIGRATION_TABLE = "migrations";
    private static final String MIGRATION_DIR = "src/main/resources/migrations"; // Path to migration scripts
    private static final String ORDER_FILE = "src/main/resources/sql-order.txt"; // Path to order file
    private static final String CHECKSUM_ALGORITHM = "SHA-256"; // Algorithm for checksum calculation

    private final MigrationExecutor executor;
    private final Connection connection;

    public MigrationManager() throws SQLException {
        this.executor = new MigrationExecutor();
        this.connection = DatabaseConnection.getConnection();
    }

    public void runMigrations() {

        try {
            // Create migration table if it doesn't exist
            createMigrationTableIfNotExists();

            // Get the list of migration scripts in the order specified in sql-order.txt
            List<String> migrationOrder = readMigrationOrder();
            List<File> migrationFiles = getMigrationFiles();

            // Execute migrations in the specified order
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

    private void createMigrationTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + MIGRATION_TABLE + " (" +
                "id SERIAL PRIMARY KEY, " +
                "version VARCHAR(50) NOT NULL, " +
                "checksum VARCHAR(64) NOT NULL, " +
                "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            logger.info("Migration table created or already exists.");
        } catch (SQLException e) {
            logger.error("Failed to create migration table: {}", e.getMessage(), e); // Log table creation error
            throw e;
        }
    }

    private List<String> readMigrationOrder() throws IOException {
        // Reading the sql-order.txt file and extracting migration execution order
        File orderFile = new File(ORDER_FILE);
        if (!orderFile.exists()) {
            logger.error("Order file does not exist: {}", ORDER_FILE);
            throw new IllegalArgumentException("Order file does not exist: " + ORDER_FILE);
        }

        return java.nio.file.Files.lines(orderFile.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.toList());
    }

    private List<File> getMigrationFiles() {
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

    private String extractVersion(File file) {
        String filename = file.getName();
        // Extract version from the filename (e.g., V1__init.sql -> 1)
        String version = filename.split("__")[0].replaceAll("[^0-9]", "");
        return version;
    }

    private String calculateChecksum(File file) throws IOException, NoSuchAlgorithmException {
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

    private boolean isMigrationApplied(String version, String checksum) throws SQLException {
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

    private void applyMigration(File file) throws SQLException {
        String sql = readSqlFromFile(file);
        executor.executeMigration(sql);
        logger.info("Executed migration from file: {}", file.getName());
    }

    private void markMigrationAsApplied(String version, String checksum) throws SQLException {
        String sql = "INSERT INTO " + MIGRATION_TABLE + " (version, checksum) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, version);
            stmt.setString(2, checksum);
            stmt.executeUpdate();
            logger.info("Marked migration as applied: version {}", version);
        }
    }

    private String readSqlFromFile(File file) {
        try {
            String sql = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            return sql;
        } catch (IOException e) {
            logger.error("Failed to read SQL file: {}", file.getName(), e);
            throw new RuntimeException("Failed to read SQL file: " + file.getName(), e);
        }
    }
}
