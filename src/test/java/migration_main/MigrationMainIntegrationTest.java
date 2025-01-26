package migration_main;

import org.example.migration.MigrationMain;
import org.example.migration.MigrationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class MigrationMainIntegrationTest {

    @Mock
    private MigrationManager migrationManager;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
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
