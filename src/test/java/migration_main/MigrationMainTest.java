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
        when(scanner.nextInt()).thenReturn(1); // Выбор: "1" (запуск миграций)
        doNothing().when(migrationManager).runMigrations();
        migrationMain.run(scanner, migrationManager);
        verify(migrationManager, times(1)).runMigrations();
    }

    @Test
    void testRollbackToValidDate() throws SQLException {
        // Arrange
        when(scanner.nextInt()).thenReturn(2); // Выбор: "2" (откат)
        when(scanner.nextLine())
                .thenReturn("") // Пропуск символа новой строки
                .thenReturn("2025-01-01 12:00:00"); // Дата отката

        LocalDateTime rollbackDate = LocalDateTime.parse("2025-01-01T12:00:00");
        doNothing().when(migrationManager).rollbackToDate(rollbackDate);

        // Act
        migrationMain.run(scanner, migrationManager);

        // Assert
        verify(migrationManager, times(1)).rollbackToDate(rollbackDate);
    }

    @Test
    void testRollbackToInvalidDate() throws SQLException {
        // Arrange
        when(scanner.nextInt()).thenReturn(2); // Выбор: "2" (откат)
        when(scanner.nextLine())
                .thenReturn("") // Пропуск символа новой строки
                .thenReturn("invalid-date"); // Некорректная дата

        // Act
        migrationMain.run(scanner, migrationManager);

        // Assert
        verify(migrationManager, never()).rollbackToDate(any());
    }

    @Test
    void testInvalidChoice() throws SQLException {
        // Arrange
        when(scanner.nextInt()).thenReturn(99); // Некорректный выбор

        // Act
        migrationMain.run(scanner, migrationManager);

        // Assert
        verifyNoInteractions(migrationManager);
    }
    @Test
    void testMain_runMigrations() {
        // Arrange
        doNothing().when(migrationManager).runMigrations();

        // Act
        migrationManager.runMigrations();

        // Assert
        verify(migrationManager, times(1)).runMigrations();
    }

    @Test
    void testMain_rollbackToDate() {
        // Arrange
        LocalDateTime rollbackDate = LocalDateTime.now();
        doNothing().when(migrationManager).rollbackToDate(rollbackDate);

        // Act
        MigrationMain.main(new String[]{"2"}); // Simulate user input choice 2
        migrationManager.rollbackToDate(rollbackDate);

        // Assert
        verify(migrationManager, times(1)).rollbackToDate(rollbackDate);
    }
}
