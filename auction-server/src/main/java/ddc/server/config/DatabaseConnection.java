package ddc.server.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://ddc-ddcauction.g.aivencloud.com:13354/ddc_auction";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_ADGuqntT-uPYf-21kYy";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
