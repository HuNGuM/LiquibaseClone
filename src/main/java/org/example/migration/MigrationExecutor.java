package org.example.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationExecutor {

    private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);

    public void executeMigration(String script) {
        logger.info("Starting migration execution with script: {}", script);

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            connection.setAutoCommit(false);
            statement.execute(script);
            connection.commit();
            logger.info("Migration executed successfully.");

        } catch (SQLException e) {
            logger.error("Error during migration execution: {}", e.getMessage());
        }
    }
}
