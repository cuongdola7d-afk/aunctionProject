package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.user.User;

public class UserDAO {

   public boolean registerUser (User user) {
      String sql = "INSERT INTO ddc_users (username, name, email, password) VALUES (?, ?, ?, ?)";

      try (Connection con = DatabaseConnection.getConnection();
           PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, user.getUsername());
            pst.setString(2, user.getName());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getPassword());

            int insert = pst.executeUpdate();
            return insert > 0;
      } catch (Exception e) {
      System.out.println(e.getMessage());
      }
      return false;
   }

   public User loginUser (String username, String password) {
      String sql = "SELECT * FROM ddc_users WHERE username = ?";

      try (Connection con = DatabaseConnection.getConnection();
           PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
               return new User.Builder()
                        .username(rs.getString("username"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .build();
            }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return null;
   }
}
