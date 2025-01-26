package migration_executor;

import org.example.migration.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.migration.MigrationExecutor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class MigrationExecutorIntegrationTest {

    private MigrationExecutor migrationExecutor;

    @BeforeEach
    void setUp() {
        migrationExecutor = new MigrationExecutor();
    }

    @Test
    void testExecuteMigration_PerformsSqlScriptOnTestDatabase() throws Exception {
        // Скрипт создаст таблицу и добавит данные
        String script = "CREATE TABLE IF NOT EXISTS test_table (id INT, name VARCHAR(255)); " +
                "INSERT INTO test_table (id, name) VALUES (1, 'Test');";

        migrationExecutor.executeMigration(script);

        // Проверяем, что таблица и данные были добавлены
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT name FROM test_table WHERE id = 1");
            assertTrue(rs.next());
            assertEquals("Test", rs.getString("name"));
        }
    }

    @Test
    void testExecuteMigration_CommitsChangesToDatabase() throws Exception {
        // Скрипт создаст таблицу и добавит данные
        String script = "CREATE TABLE IF NOT EXISTS another_table (id INT, description VARCHAR(255)); " +
                "INSERT INTO another_table (id, description) VALUES (1, 'Description');";

        migrationExecutor.executeMigration(script);

        // Проверяем, что изменения были зафиксированы
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT description FROM another_table WHERE id = 1");
            assertTrue(rs.next());
            assertEquals("Description", rs.getString("description"));
        }
    }
}
