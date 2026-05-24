package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.user.User;

public class AdminDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDAO.class);

    private final UserDAO userDAO = new UserDAO();

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection con = getConnection()) {
            String sql = "SELECT * FROM ddc_users ORDER BY username";

            try (PreparedStatement pst = con.prepareStatement(sql);
                    ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    users.add(userDAO.mapUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Loi lay danh sach user cho admin", e);
        }
        return users;
    }

    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM ddc_users WHERE id = ?";

        try (Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, userId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Loi xoa user", e);
        }
        return false;
    }

    public boolean updateUserStatus(String userId, String status) {
        if (!isAllowedUserStatus(status)) {
            LOGGER.warn("Trang thai user khong hop le: {}", status);
            return false;
        }

        String sql = "UPDATE ddc_users SET status = ? WHERE id = ?";

        try (Connection con = getConnection()) {
            if (!hasColumn(con, "ddc_users", "status")) {
                LOGGER.warn("Bang ddc_users chua co cot status.");
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, status.toUpperCase());
                pst.setString(2, userId);
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Loi cap nhat trang thai user", e);
        }
        return false;
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("users", countUsers());
        stats.put("items", count("ddc_items"));
        stats.put("auctions", count("ddc_auctions"));
        stats.put("runningAuctions", countWhere("ddc_auctions", "status = 'RUNNING'"));
        return stats;
    }

    private int countUsers() {
        return count("ddc_users");
    }

    private boolean isAllowedUserStatus(String status) {
        return "ACTIVE".equalsIgnoreCase(status) || "BLOCKED".equalsIgnoreCase(status);
    }

    private int count(String tableName) {
        return countWhere(tableName, null);
    }

    private int countWhere(String tableName, String whereClause) {
        String sql = "SELECT COUNT(*) FROM " + tableName + (whereClause == null ? "" : " WHERE " + whereClause);

        try (Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.warn("Khong dem duoc bang {}", tableName, e);
        }
        return 0;
    }

    private boolean hasColumn(Connection con, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = con.createStatement().executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Connection getConnection() throws SQLException {
        return ddc.server.config.DatabaseConnection.getConnection();
    }
}
