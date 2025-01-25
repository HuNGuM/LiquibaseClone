package migrations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationScriptExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MigrationScriptExecutor.class);

    private final Connection connection;

    public MigrationScriptExecutor(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes the migration SQL script.
     *
     * @param sql SQL query to execute.
     * @throws SQLException if an error occurs while executing the query.
     */
    public void execute(String sql) throws SQLException {
        logger.info("Starting migration with SQL: {}", sql);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Ensuring transactional integrity of the migration execution
            connection.setAutoCommit(false);
            preparedStatement.execute();
            connection.commit();
            logger.info("Migration executed successfully.");
        } catch (SQLException e) {
            logger.error("Error executing migration: {}", e.getMessage());
            connection.rollback();  // Rollback in case of error
            throw e;
        } finally {
            // Restoring autocommit to its original state
            connection.setAutoCommit(true);
            logger.info("Autocommit restored to its original state.");
        }
    }
}
