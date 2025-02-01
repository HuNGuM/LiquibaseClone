package org.example.migration;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DataSource dataSource;

    static {
        BasicDataSource ds = new BasicDataSource();
        String url = System.getenv("URL");
        String user = System.getenv("USER");
        String password = System.getenv("PASSWORD");
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setInitialSize(5);
        ds.setMaxTotal(10);
        dataSource = ds;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}