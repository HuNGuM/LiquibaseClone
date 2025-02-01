package migration_manager;

import org.example.migration.MigrationManager;
import org.junit.jupiter.api.*;
import org.testcontainers.utility.MountableFile;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MigrationManagerIntegrationTest {

    private static PostgreSQLContainer<?> postgresContainer;
    private MigrationManager migrationManager;
    private Connection connection;

    @BeforeAll
    static void setUpBeforeAll() {
        postgresContainer = new PostgreSQLContainer<>("postgres:13")
                .withDatabaseName("test_db")
                .withUsername("test")
                .withPassword("test")
                .withCopyFileToContainer(MountableFile.forClasspathResource("migrations/"), "/migrations")
                .withCopyFileToContainer(MountableFile.forClasspathResource("sql-order.txt"), "/sql-order.txt");

        postgresContainer.start();
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(), postgresContainer.getUsername(), postgresContainer.getPassword());
        migrationManager = new MigrationManager();
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS migrations");
        }
    }

    @AfterAll
    static void tearDownAfterAll() {
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
    }

    @Test
    void testRunMigrations() {
        try {
            migrationManager.runMigrations();

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM migrations");
                rs.next();
                int count = rs.getInt(1);
                assertTrue(count > 0, "Migration table should have at least one entry.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRollbackMigrations() {
        try {
            migrationManager.runMigrations();

            LocalDateTime rollbackDate = LocalDateTime.now();
            migrationManager.rollbackToDate(rollbackDate);

            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM migrations");
                rs.next();
                int count = rs.getInt(1);
                assertEquals(0, count, "Migration table should be empty after rollback.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testChecksumComparison() {
        try {
            File migrationFile = new File("src/main/resources/migrations/V1__init.sql");
            String checksum = migrationManager.calculateChecksum(migrationFile);

            boolean isApplied = migrationManager.isMigrationApplied("1", checksum);

            assertFalse(isApplied, "Migration should not be applied yet.");
        } catch (SQLException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
