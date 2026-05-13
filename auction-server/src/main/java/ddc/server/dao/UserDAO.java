package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.user.User;

public class UserDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

    public boolean registerUser(User user) {
        String sql = "INSERT INTO ddc_users (username, name, email, password) VALUES (?, ?, ?, ?)";

        if (user == null || isBlank(user.getUsername()) || isBlank(user.getEmail()) || isBlank(user.getPassword())) {
            return false;
        }

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, user.getUsername().trim());
            pst.setString(2, user.getName());
            pst.setString(3, user.getEmail().trim());
            pst.setString(4, user.getPassword());

            int insert = pst.executeUpdate();
            return insert > 0;
        } catch (SQLException e) {
            LOGGER.warn("Khong the dang ky user.", e);
        }
        return false;
    }

    public User getUser(String username) {
        String sql = "SELECT * FROM ddc_users WHERE username = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username.trim());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    User user = new User()
                            .setId(rs.getString("id"))
                            .setUsername(rs.getString("username"))
                            .setName(rs.getString("name"))
                            .setEmail(rs.getString("email"))
                            .setPassword(rs.getString("password"));
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Khong the dang nhap user.", e);
        }
        return null;
    }

    // Lấy user theo id (primary key)
    public User getUserById(String id) {
        String sql = "SELECT * FROM ddc_users WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new User()
                            .setId(rs.getString("id"))
                            .setUsername(rs.getString("username"))
                            .setName(rs.getString("name"))
                            .setEmail(rs.getString("email"))
                            .setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Không tìm được user theo id.", e);
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean changePassword(String username, String newPassword) {
        // Câu lệnh SQL để cập nhật mật khẩu
        String sql = "UPDATE ddc_users SET password = ? WHERE username = ?";

        // Sử dụng try-with-resources để tự động đóng Connection và PreparedStatement
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Gán các giá trị vào dấu "?"
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            // Thực thi lệnh Update
            int rowsAffected = pstmt.executeUpdate();
            LOGGER.debug("changePassword rowsAffected: {}", rowsAffected);

            // Nếu số dòng bị ảnh hưởng > 0 tức là đã cập nhật thành công
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.error("Loi khi thuc thi changePassword SQL", e);
            return false;
        }
    }

    public boolean updateUserProfile(User user) {
        String sql = "UPDATE ddc_users SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set các tham số theo thứ tự dấu hỏi chấm
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getId());

            // executeUpdate trả về số dòng bị tác động
            int rowsAffected = pstmt.executeUpdate();
            // Nếu > 0 nghĩa là đã cập nhật thành công ít nhất 1 dòng
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.error("Loi khi cap nhat thong tin User: {}", e.getMessage(), e);
            return false;
        }
    }
}
