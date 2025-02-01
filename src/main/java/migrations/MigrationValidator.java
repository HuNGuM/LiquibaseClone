package migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class MigrationValidator {

    private static final Logger logger = LoggerFactory.getLogger(MigrationValidator.class);
    private final Connection connection;

    public MigrationValidator(Connection connection) {
        this.connection = connection;
    }

    /**
     * Validates the syntax of the SQL script.
     *
     * @param sql SQL query to validate.
     * @return true if the query is syntactically correct, otherwise false.
     * @throws SQLException if an error occurs during validation.
     */
    public boolean validate(String sql) throws SQLException {
        logger.info("Validating SQL syntax: {}", sql);

        try (var stmt = connection.createStatement()) {
            stmt.executeQuery("EXPLAIN " + sql);
            logger.info("SQL syntax is valid.");
            return true;
        } catch (SQLException e) {
            logger.error("SQL syntax is invalid: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the script attempts to perform destructive operations (e.g., drop tables).
     *
     * @param sql SQL query to check.
     * @return true if the script does not contain destructive operations.
     */
    public boolean isSafe(String sql) {
        if (sql.toLowerCase().contains("drop") || sql.toLowerCase().contains("delete")) {
            logger.warn("Destructive operation detected in SQL: {}", sql);
            return false;
        }
        logger.info("SQL is safe for execution.");
        return true;
    }
}
