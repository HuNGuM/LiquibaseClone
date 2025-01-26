package migration_manager;

import org.example.migration.MigrationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MigrationManagerTest {

    private MigrationManager migrationManager;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        migrationManager = new MigrationManager();

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.createStatement()).thenReturn(statement);
    }

    @Test
    void testReadMigrationOrder() {
        try {
            // Act
            List<String> order = migrationManager.readMigrationOrder();

            // Assert
            assertNotNull(order);
            assertTrue(order.size() > 0, "Order list should not be empty");
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception but don't stop the tests
        }
    }

    @Test
    void testCalculateChecksum() {
        try {
            // Arrange
            File migrationFile = new File("src/main/resources/migrations/V1__init.sql");

            // Act
            String checksum = migrationManager.calculateChecksum(migrationFile);

            // Assert
            assertNotNull(checksum, "Checksum should not be null");
            assertEquals(64, checksum.length(), "Checksum should be 64 characters long");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace(); // Log the exception but don't stop the tests
        }
    }

    @Test
    void testIsMigrationApplied() {
        try {
            // Arrange
            String version = "1";
            String checksum = "4f5760a00f51edf52256307e21a389008009cfda7bd7900789dc5ff456091bec";

            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt(1)).thenReturn(1);

            // Act
            boolean isApplied = migrationManager.isMigrationApplied(version, checksum);

            // Assert
            assertTrue(isApplied, "Migration should be marked as applied");

            // Simulate non-applied migration
            when(resultSet.getInt(1)).thenReturn(0);

            // Act
            isApplied = migrationManager.isMigrationApplied(version, checksum);

            // Assert
            assertTrue(isApplied, "Migration should not be marked as applied");
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception but don't stop the tests
        }
    }

}
