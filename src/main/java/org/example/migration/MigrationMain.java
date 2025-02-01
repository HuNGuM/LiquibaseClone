package org.example.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MigrationMain {

    private static final Logger logger = LoggerFactory.getLogger(MigrationMain.class);

    public static void main(String[] args) {
        logger.info("Migration management program started...");
        try (Scanner scanner = new Scanner(System.in)) {
            MigrationManager manager = new MigrationManager();
            new MigrationMain().run(scanner, manager);
        } catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage(), e);
        }
    }

    public void run(Scanner scanner, MigrationManager manager) throws SQLException {
        logger.info("Select an action:");
        logger.info("1: Run migrations");
        logger.info("2: Rollback to a specific date");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> {
                logger.info("Running migrations...");
                manager.runMigrations();
                logger.info("Migrations completed successfully.");
            }
            case 2 -> {
                logger.info("Enter the rollback date in the format 'YYYY-MM-DD HH:MM:SS':");
                scanner.nextLine();
                String dateString = scanner.nextLine();
                handleRollback(dateString, manager);
            }
            default -> logger.warn("Invalid choice. Program will exit.");
        }
    }

    private void handleRollback(String dateString, MigrationManager manager) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime rollbackDate = LocalDateTime.parse(dateString, formatter);

            logger.info("Attempting to rollback migrations to {}", rollbackDate);
            manager.rollbackToDate(rollbackDate);
            logger.info("Rollback completed to {}", rollbackDate);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format: {}", dateString);
        }
    }
}
