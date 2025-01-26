import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    public static Connection getConnection() throws SQLException, IOException {
        Properties properties = new Properties();

        // Чтение настроек из testApplication.properties
        properties.load(DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties"));

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        String driverClass = properties.getProperty("db.driverClass");

        try {
            Class.forName(driverClass); // Загружаем драйвер
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver not found", e);
        }

        return DriverManager.getConnection(url, username, password); // Подключаемся к базе данных
    }
}
