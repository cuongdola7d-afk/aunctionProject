package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.item.*;

public class ItemDAO {

   public boolean addItem (Item item) {
      String sql = "INSERT INTO ddc_items (item_name, category, description, seller_name) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, item.getId());
            pst.setString(2, item.getItemName());
            pst.setString(3, item.getCategory());
            pst.setString(4, item.getDescription());
            pst.setString(5, item.getSellerName());

            int insert = pst.executeUpdate();
            return insert > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Item getItem(String id) {
        String sql = "SELECT * FROM ddc_items WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String itemId = rs.getString("id");
                String itemName = rs.getString("item");
                String category = rs.getString("category");
                String description = rs.getString("description");
                String seller = rs.getString("seller");

                Item item = buildItemByCategory(category);
                if (item != null) {
                    item.setId(itemId);
                    item.setItemName(itemName);
                    item.setDescription(description);
                    item.setSellerName(seller);
                    item.setStartingPrice(0);
                    item.setCurrentPrice(0);
                }
                return item;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Item buildItemByCategory(String category) {
        if (category == null) {
            return null;
        }

        switch (category.toUpperCase()) {
            case "ART":
                return new Art();
            case "ELECTRONICS":
                return new Electronics();
            case "VEHICLE":
                return new Vehicle();
            default:
                return null;
        }
    }
}