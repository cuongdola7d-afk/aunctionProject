package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
}