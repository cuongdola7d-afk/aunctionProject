package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ddc.server.config.DatabaseConnection;
import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.CreatorRegistry;
import ddc.server.pattern.factory.ItemRequest;

public class ItemDAO {
    private static final Logger LOGGER = Logger.getLogger(ItemDAO.class.getName());

    public String addItem(ItemGeneric item) {
        if (item == null) {
            return null;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String id = item.save(con);
                con.commit();
                return id;
            } catch (SQLException e) {
                con.rollback();
                LOGGER.log(Level.WARNING, "Khong the luu item.", e);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the mo ket noi DB de luu item.", e);
            return null;
        }
    }

    public ItemGeneric getItem(String id) {
        String sql = "SELECT * FROM ddc_items WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id.trim());

            ResultSet rs = pst.executeQuery();

            // BẮT BUỘC PHẢI GỌI rs.next() Ở ĐÂY
            if (rs.next()) { 
                ItemRequest request = new ItemRequest(
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("seller_name")
                );

                String category = rs.getString("category");
                String itemId = rs.getString("id");

                ItemGeneric item = CreatorRegistry.getCreator(category).createItem(request);
                System.out.println(item.getItemName());

                item.setId(itemId);
                item.load(con);
                
                try {
                    item.validate();
                } catch (ItemValidationException e) {
                    e.printStackTrace();
                }
                return item;            
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ItemValidationException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
