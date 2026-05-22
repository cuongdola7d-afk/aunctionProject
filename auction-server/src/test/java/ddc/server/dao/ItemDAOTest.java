package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.item.Art;
import ddc.server.model.item.ItemGeneric;

public class ItemDAOTest {
    private final ItemDAO itemDAO = new ItemDAO();

    @Test
    void testAddItem_Success() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        String generatedId = null;
        try {
            Art art = new Art()
                    .setItemName("Tranh Test ID Tu Dong")
                    .setSellerName("cuongdo123")
                    .setyearCreated(1999)
                    .setDescription("abc")
                    .setAuthor("Artist Test");

            generatedId = itemDAO.addItem(art);

            assertNotNull(generatedId, "DAO phai tra ve ID ");
            assertFalse(generatedId.trim().isEmpty(), "ID tra ve khong duoc rong");
        } finally {
            deleteTestItem(generatedId);
        }
    }

    @Test
    void testGetItem_Success() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        String existingIdInDB = "I00005";
        ItemGeneric<?> retrieved = itemDAO.getItem(existingIdInDB);

        assertNotNull(retrieved, "Phai lay ra duoc item co san trong DB");
        assertEquals(existingIdInDB, retrieved.getId());
    }

    private boolean hasDbConfig() {
        return hasValue("DDC_DB_URL") && hasValue("DDC_DB_USER") && hasValue("DDC_DB_PASSWORD");
    }

    private boolean hasValue(String envName) {
        String value = System.getenv(envName);
        return value != null && !value.isBlank();
    }

    private void deleteTestItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                deleteById(con, "item_art", itemId);
                deleteById(con, "ddc_items", itemId);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Khong the xoa du lieu test item: " + itemId, e);
        }
    }

    private void deleteById(Connection con, String tableName, String itemId) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, itemId);
            pst.executeUpdate();
        }
    }
}
