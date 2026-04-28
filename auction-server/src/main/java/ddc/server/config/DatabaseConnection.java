package ddc.server.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = readConfig("DDC_DB_URL", "ddc.db.url");
    private static final String USER = readConfig("DDC_DB_USER", "ddc.db.user");
    private static final String PASSWORD = readConfig("DDC_DB_PASSWORD", "ddc.db.password");

    public static Connection getConnection() throws SQLException {
        if (isBlank(URL) || isBlank(USER) || isBlank(PASSWORD)) {
            throw new SQLException("Thieu cau hinh DB. Can DDC_DB_URL, DDC_DB_USER, DDC_DB_PASSWORD.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String readConfig(String envName, String propertyName) {
        String value = System.getenv(envName);
        if (!isBlank(value)) {
            return value;
        }
        return System.getProperty(propertyName);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
