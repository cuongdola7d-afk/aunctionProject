package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.item.Art;
import ddc.server.model.item.Electronics;
import ddc.server.model.item.ItemGeneric;
import ddc.server.model.item.Vehicle;

public class ItemDAO {

   public String addItem (ItemGeneric item) {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String id = item.save(con);

                con.commit();
                return id;
            } catch (SQLException e) {
                con.rollback();
                e.printStackTrace();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
    }

    public ItemGeneric getItem(String id) {
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

                ItemGeneric item = buildItemByCategory(category);
                if (item != null) {
                    item.setId(itemId);
                    item.setItemName(itemName);
                    item.setDescription(description);
                    item.setSellerName(seller);
                }
                return item;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private ItemGeneric buildItemByCategory(String category) {
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