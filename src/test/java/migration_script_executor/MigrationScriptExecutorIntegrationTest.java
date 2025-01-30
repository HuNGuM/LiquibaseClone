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
        // Инициализация подключения к тестовой базе данных H2
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        migrationScriptExecutor = new MigrationScriptExecutor(connection);

        // Создаем таблицу для тестов
        try (Statement statement = connection.createStatement()) {
            // Убедитесь, что таблица существует до выполнения SQL
            statement.execute("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, name VARCHAR(255))");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Очистка тестовой базы данных
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS test_table");
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    @DisplayName("Execute migration successfully and insert data")
    void testExecuteMigrationSuccess() throws SQLException {
        // Выполнение SQL-скрипта
        String sql = "INSERT INTO test_table (id, name) VALUES (1, 'Test Name')";
        migrationScriptExecutor.execute(sql);

        // Проверка, что данные были добавлены в таблицу
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_table WHERE id = 1");

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                assertEquals(1, count, "Таблица должна содержать 1 запись с id = 1");
            } else {
                fail("Таблица не содержит записей с id = 1");
            }
        }
    }

    @Test
    @DisplayName("Rollback migration on failure")
    void testExecuteMigrationRollbackOnFailure() throws SQLException {
        // Миграция с ошибкой (например, нарушение ограничения уникальности)
        String sql = "INSERT INTO test_table (id, name) VALUES (1, 'Test Name')";
        migrationScriptExecutor.execute(sql);

        // Попытка вставить строку с таким же id, что вызовет ошибку
        String invalidSql = "INSERT INTO test_table (id, name) VALUES (1, 'Another Test Name')";

        SQLException exception = assertThrows(SQLException.class, () -> {
            migrationScriptExecutor.execute(invalidSql);
        });

        // Проверка, что транзакция откатилась после ошибки
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_table");

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                assertEquals(1, count, "Таблица должна содержать только 1 запись после отката");
            } else {
                fail("Таблица пуста после отката");
            }
        }
    }

    @Test
    @DisplayName("Auto-commit restored after migration execution")
    void testAutoCommitRestoredAfterExecution() throws SQLException {
        // Проверка, что AutoCommit был восстановлен после выполнения миграции
        boolean initialAutoCommit = connection.getAutoCommit();

        String sql = "INSERT INTO test_table (id, name) VALUES (2, 'New Test Name')";
        migrationScriptExecutor.execute(sql);

        // Проверка, что AutoCommit восстановлен в true после выполнения миграции
        assertTrue(connection.getAutoCommit(), "AutoCommit должен быть восстановлен в true");

        // Восстановление состояния соединения
        connection.setAutoCommit(initialAutoCommit);
    }
}
