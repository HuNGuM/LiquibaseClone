package migration_main;

import org.example.migration.MigrationMain;
import org.example.migration.MigrationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class MigrationMainTest {

    @Mock
    private MigrationManager migrationManager;

    @Mock
    private Scanner scanner;

    private MigrationMain migrationMain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        migrationMain = new MigrationMain();
    }

    @Test
    void testRunMigrationsSuccessfully() throws SQLException {
        // Arrange
        when(scanner.nextInt()).thenReturn(1);
        doNothing().when(migrationManager).runMigrations();
        migrationMain.run(scanner, migrationManager);
        verify(migrationManager, times(1)).runMigrations();
    }

    @Test
    void testRollbackToValidDate() throws SQLException {
        when(scanner.nextInt()).thenReturn(2);
        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("2025-01-01 12:00:00");

        LocalDateTime rollbackDate = LocalDateTime.parse("2025-01-01T12:00:00");
        doNothing().when(migrationManager).rollbackToDate(rollbackDate);

        migrationMain.run(scanner, migrationManager);

        verify(migrationManager, times(1)).rollbackToDate(rollbackDate);
    }

    @Test
    void testRollbackToInvalidDate() throws SQLException {
        when(scanner.nextInt()).thenReturn(2);
        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("invalid-date");

        migrationMain.run(scanner, migrationManager);

        verify(migrationManager, never()).rollbackToDate(any());
    }

    @Test
    void testInvalidChoice() throws SQLException {
        when(scanner.nextInt()).thenReturn(99);

        migrationMain.run(scanner, migrationManager);

        verifyNoInteractions(migrationManager);
    }
    @Test
    void testMain_runMigrations() {
        doNothing().when(migrationManager).runMigrations();

        migrationManager.runMigrations();

        verify(migrationManager, times(1)).runMigrations();
    }

    @Test
    void testMain_rollbackToDate() {
        LocalDateTime rollbackDate = LocalDateTime.now();
        doNothing().when(migrationManager).rollbackToDate(rollbackDate);

        MigrationMain.main(new String[]{"2"});
        migrationManager.rollbackToDate(rollbackDate);

        verify(migrationManager, times(1)).rollbackToDate(rollbackDate);
    }
}
