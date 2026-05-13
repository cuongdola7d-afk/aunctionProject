package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.config.DatabaseConnection;
import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.CreatorRegistry;
import ddc.server.pattern.factory.ItemRequest;

public class ItemDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDAO.class);

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
                LOGGER.warn("Khong the luu item: {}", e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            LOGGER.warn("Khong the mo ket noi DB de luu item: {}", e.getMessage());
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
                        rs.getString("seller_name"),
                        rs.getString("image_url"));

                String category = rs.getString("category");
                String itemId = rs.getString("id");

                ItemGeneric item = CreatorRegistry.getCreator(category).createItem(request);
                LOGGER.info("Lay item: {}", item.getItemName());

                item.setId(itemId);
                item.load(con);

                try {
                    item.validate();
                } catch (ItemValidationException e) {
                    LOGGER.warn("Loi validation item: {}", e.getMessage());
                }
                return item;
            }

        } catch (SQLException e) {
            LOGGER.warn("Loi SQL: {}", e.getMessage());
        } catch (ItemValidationException e) {
            LOGGER.warn("Loi validation item: {}", e.getMessage());
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
