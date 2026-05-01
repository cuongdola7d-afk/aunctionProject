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
import ddc.server.pattern.factory.ItemCreator;
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
        if (isBlank(id)) {
            return null;
        }

        String sql = "SELECT * FROM ddc_items WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id.trim());
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String category = rs.getString("category");
                ItemCreator creator = CreatorRegistry.getCreator(category);
                if (creator == null) {
                    LOGGER.log(Level.WARNING, "Category trong DB khong hop le: {0}", category);
                    return null;
                }

                ItemRequest request = new ItemRequest(
                        rs.getString("item_name"),
                        rs.getString("description"),
                        category,
                        rs.getString("seller_name"));

                ItemGeneric item = creator.createItem(request);
                item.setId(rs.getString("id"));
                item.load(con);
                item.validate();
                return item;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the lay item tu DB.", e);
        } catch (ItemValidationException e) {
            LOGGER.log(Level.WARNING, "Du lieu item trong DB khong hop le.", e);
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
