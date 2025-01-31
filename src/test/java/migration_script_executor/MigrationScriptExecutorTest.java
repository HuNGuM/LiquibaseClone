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

    @Mock private Connection connection;
    @Mock private PreparedStatement preparedStatement;
    @Mock private ResultSet resultSet;
    @Mock private SQLException sqlException;

    private MigrationScriptExecutor migrationScriptExecutor;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        migrationScriptExecutor = new MigrationScriptExecutor(connection);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
    }

    @Test
    @DisplayName("Execute migration successfully")
    void testExecuteMigrationSuccess() throws SQLException {
        String sql = "CREATE TABLE test(id INT)";
        migrationScriptExecutor.execute(sql);

        verify(connection).setAutoCommit(false);
        verify(preparedStatement).execute();
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    @DisplayName("Rollback on migration failure")
    void testExecuteMigrationRollbackOnFailure() throws SQLException {
        String sql = "CREATE TABLE test(id INT)";
        doThrow(sqlException).when(preparedStatement).execute();

        assertThrows(SQLException.class, () -> migrationScriptExecutor.execute(sql));

        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
    }
}