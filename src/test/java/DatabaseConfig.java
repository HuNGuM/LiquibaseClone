import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    public static Connection getConnection() throws SQLException, IOException {
        Properties properties = new Properties();

        properties.load(DatabaseConfig.class.getClassLoader().getResourceAsStream("testApplication.properties"));

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        String driverClass = properties.getProperty("db.driverClass");

        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver not found", e);
        }

        return DriverManager.getConnection(url, username, password);
    }
}
