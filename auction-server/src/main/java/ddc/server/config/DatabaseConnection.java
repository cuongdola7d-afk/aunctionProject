package ddc.server.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig; //Settings của Pool
import com.zaxxer.hikari.HikariDataSource; //Pool dựa trên config
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final String URL = EnvConfig.get("DDC_DB_URL", "ddc.db.url");
    private static final String USER = EnvConfig.get("DDC_DB_USER", "ddc.db.user");
    private static final String PASSWORD = EnvConfig.get("DDC_DB_PASSWORD", "ddc.db.password");

    private static HikariDataSource dataSource;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);
    // Khối static để cấu hình Pool ngay khi lớp được load
    static {
        if (isBlank(URL) || isBlank(USER) || isBlank(PASSWORD)) {
            LOGGER.error("Thieu cau hinh DB: URL/USER/PASSWORD");
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);

            config.setMaximumPoolSize(20);
            config.setConnectionTimeout(30000);

            // Thời gian tối đa một kết nối được phép rảnh trước khi bị đóng
            config.setIdleTimeout(600000);
            config.setPoolName("DDC_Auction_Pool");

            config.addDataSourceProperty("cachePrepStmts", "true"); // Ghi nhớ câu lệnh.
            config.addDataSourceProperty("prepStmtCacheSize", "250"); // Độ dài tối đa của câu lệnh được phép ghi nhớ
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // Độ dài tối đa của câu lệnh được phép ghi
                                                                           // nhớ

            dataSource = new HikariDataSource(config);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource chưa được khởi tạo. Kiểm tra lại cấu hình DB.");
        }
        // Thay vì DriverManager, ta lấy từ dataSource
        return dataSource.getConnection();
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
