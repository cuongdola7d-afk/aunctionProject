package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.user.User;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO ddc_users (id, username, name, email, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, user.getId());
            pst.setString(2, user.getUsername());
            pst.setString(3, user.getName());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getPassword());

            int insert = pst.executeUpdate();
            return insert > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM ddc_users WHERE username = ? AND password = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User()
                .setId(rs.getString("id"))
                .setUsername(rs.getString("username"))
                .setName(rs.getString("name"))
                .setEmail(rs.getString("email"))
                .setPassword(rs.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
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
            System.out.println(rowsAffected);

            // Nếu số dòng bị ảnh hưởng > 0 tức là đã cập nhật thành công
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thực thi changePassword SQL:");
            e.printStackTrace();
            return false;
        }
    }
}