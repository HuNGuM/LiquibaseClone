package migration_script_executor;

import migrations.MigrationScriptExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MigrationScriptExecutorTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private SQLException sqlException;

    private MigrationScriptExecutor migrationScriptExecutor;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        migrationScriptExecutor = new MigrationScriptExecutor(connection);

        // Настроим mock-объекты
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    @DisplayName("Test with custom name: Execute migration successfully")
    void testExecuteMigrationSuccess() throws SQLException {
        String sql = "CREATE TABLE test(id INT)";

        // Выполнение миграции
        migrationScriptExecutor.execute(sql);

        // Проверка, что prepareStatement был вызван с нужным SQL
        verify(connection).prepareStatement(sql);
        verify(preparedStatement).execute();
        verify(connection).commit();
        verify(preparedStatement).close();
        verify(connection).setAutoCommit(true);
    }

    @Test
    @DisplayName("Test with custom name: Execute migration rollback on failure")
    void testExecuteMigrationRollbackOnFailure() throws SQLException {
        String sql = "CREATE TABLE test(id INT)";

        // Моделируем исключение при выполнении SQL
        doThrow(sqlException).when(preparedStatement).execute();

        // Проверяем, что SQLException выбрасывается
        assertThrows(SQLException.class, () -> {
            migrationScriptExecutor.execute(sql);
        });

        // Проверяем, что транзакция откатилась
        verify(connection).rollback();
        verify(connection).setAutoCommit(true);  // Убедимся, что AutoCommit восстанавливается
    }

    @Test
    @DisplayName("Test with custom name: Auto-commit restored after execution")
    void testAutoCommitRestoredAfterExecution() throws SQLException {
        String sql = "CREATE TABLE test(id INT)";

        // Моделируем, что AutoCommit было отключено
        when(connection.getAutoCommit()).thenReturn(false);

        migrationScriptExecutor.execute(sql);

        // Проверка, что AutoCommit был восстановлен в конце
        verify(connection).setAutoCommit(true);
    }
}
