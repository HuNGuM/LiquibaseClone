package migration_script_executor;

import migrations.MigrationScriptExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class MigrationScriptExecutorIntegrationTest {

    private Connection connection;
    private MigrationScriptExecutor migrationScriptExecutor;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        migrationScriptExecutor = new MigrationScriptExecutor(connection);

        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS test_table");
            statement.execute("DROP TABLE IF EXISTS migration_lock");

            statement.execute("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, name VARCHAR(255))");
            statement.execute("CREATE TABLE IF NOT EXISTS migration_lock (id INT PRIMARY KEY, is_locked BOOLEAN)");
            statement.execute("MERGE INTO migration_lock (id, is_locked) KEY(id) VALUES (1, FALSE)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE IF EXISTS test_table");
                statement.execute("DROP TABLE IF EXISTS migration_lock");
            }
            connection.close();
        }
    }

    @Test
    @DisplayName("Skip execution when migration is locked")
    void testSkipExecutionWhenLocked() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE migration_lock SET is_locked = TRUE WHERE id = 1")) {
            preparedStatement.executeUpdate();
        }

        String sql = "INSERT INTO test_table (id, name) VALUES (1, 'Test Name')";
        migrationScriptExecutor.execute(sql);

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM test_table")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            assertEquals(0, resultSet.getInt(1), "Данные не должны были вставиться, так как миграция была заблокирована");
        }
    }


    private void unlockMigration() throws SQLException {
        String unlockMigrationQuery = "UPDATE migration_lock SET is_locked = FALSE WHERE id = 1";
        try (PreparedStatement unlockStmt = connection.prepareStatement(unlockMigrationQuery)) {
            unlockStmt.executeUpdate();
        }
    }
}