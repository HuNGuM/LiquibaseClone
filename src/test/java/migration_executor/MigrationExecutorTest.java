package migration_executor;

import org.example.migration.DatabaseConnection;
import org.example.migration.MigrationExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.*;

class MigrationExecutorTest {

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    private MigrationExecutor migrationExecutor;

    @Captor
    private ArgumentCaptor<String> scriptCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        migrationExecutor = new MigrationExecutor();

        try (var mock = mockStatic(DatabaseConnection.class)) {
            mock.when(DatabaseConnection::getConnection).thenReturn(connection);
        }
    }

    @Test
    void testExecuteMigration_CorrectErrorHandling() throws SQLException {
        String script = "SELECT * FROM test_table";

        when(connection.createStatement()).thenReturn(statement);
        doThrow(new SQLException("SQL error")).when(statement).execute(script);

        migrationExecutor.executeMigration(script);
    }
}
