package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.user.User;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

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
            LOGGER.log(Level.WARNING, "Khong the dang ky user.", e);
        }
        return false;
    }

    public User loginUser(String username, String password) {
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
                        .setPassword(null);
                        return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the dang nhap user.", e);
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
