package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.item.Item;

public class ItemDAO {

   public boolean addItem (Item item) {
      String sql = "INSERT INTO ddc_items (item, category, description, seller) VALUES (?, ?, ?, ?)";

      try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)) {
               
               pst.setString(1, item.getItemName());
               pst.setString(2, item.getCategory());
               pst.setString(3, item.getDescription());
               pst.setString(4, item.getSellerName());

               int insert = pst.executeUpdate();
               return insert > 0;
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
            return false;
   }

   public Item getItem (String id) {
      String sql = "SELECT * FROM ddc_items WHERE id = ?";

      try (Connection con = DatabaseConnection.getConnection();
           PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
               return new Item.Builder()
                        .id(rs.getString("id"))
                        .item(rs.getString("item"))
                        .category(rs.getString("category"))
                        .description(rs.getString("description"))
                        .seller(rs.getString("seller"))
                        .build();
            }
           } catch (SQLException e) {
            System.out.println(e.getMessage());
           }
           return null;
   }
}