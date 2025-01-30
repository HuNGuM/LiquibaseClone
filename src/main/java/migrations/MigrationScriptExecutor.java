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

        // Проверка и установка блокировки
        if (!lockMigration()) {
            logger.warn("Migration is already locked. Skipping execution.");
            return;
        }

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
            // Сброс блокировки
            unlockMigration();
            // Restoring autocommit to its original state
            connection.setAutoCommit(true);
            logger.info("Autocommit restored to its original state.");
        }
    }

    private boolean lockMigration() throws SQLException {
        String checkLockQuery = "SELECT is_locked FROM migration_lock WHERE id = 1";  // Статическая запись для блокировки
        try (PreparedStatement checkLockStmt = connection.prepareStatement(checkLockQuery)) {
            boolean isLocked = checkLockStmt.executeQuery().next();
            if (isLocked) {
                return false;
            }

            String lockMigrationQuery = "UPDATE migration_lock SET is_locked = TRUE WHERE id = 1";
            try (PreparedStatement lockStmt = connection.prepareStatement(lockMigrationQuery)) {
                lockStmt.executeUpdate();
            }

            return true;
        }
    }

    private void unlockMigration() throws SQLException {
        String unlockMigrationQuery = "UPDATE migration_lock SET is_locked = FALSE WHERE id = 1";
        try (PreparedStatement unlockStmt = connection.prepareStatement(unlockMigrationQuery)) {
            unlockStmt.executeUpdate();
        }
    }
}
