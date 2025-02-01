package migration_validator;

import migrations.MigrationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class MigrationValidatorTest {

    private Connection connection;
    private MigrationValidator migrationValidator;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        migrationValidator = new MigrationValidator(connection);
    }

    @Test
    @DisplayName("Validate correct SQL syntax")
    void testValidateCorrectSyntax() throws SQLException {
        String sql = "SELECT * FROM test_table";

        when(connection.createStatement()).thenReturn(mock(java.sql.Statement.class));

        boolean result = migrationValidator.validate(sql);

        assertTrue(result, "SQL должен быть корректным");
    }

    @Test
    @DisplayName("Validate incorrect SQL syntax")
    void testValidateIncorrectSyntax() throws SQLException {
        String sql = "SELECT * FROM non_existing_table";

        when(connection.createStatement()).thenThrow(new SQLException("Syntax error"));

        boolean result = migrationValidator.validate(sql);

        assertFalse(result, "SQL должен быть некорректным");
    }

    @Test
    @DisplayName("Check if script contains destructive operations")
    void testIsSafeWithDestructiveOperations() {
        String sql = "DROP TABLE test_table";

        boolean result = migrationValidator.isSafe(sql);

        assertFalse(result, "SQL должен быть признан небезопасным");
    }

    @Test
    @DisplayName("Check if script is safe")
    void testIsSafeWithSafeScript() {
        String sql = "SELECT * FROM test_table";

        boolean result = migrationValidator.isSafe(sql);

        assertTrue(result, "SQL должен быть признан безопасным");
    }
}
