package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

import ddc.server.model.item.Art;
import ddc.server.model.item.ItemGeneric;

public class ItemDAOTest {
    private final ItemDAO itemDAO = new ItemDAO();

    @Test
    void testAddItem_Success() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        Art art = new Art()
                .setItemName("Tranh Test ID Tu Dong")
                .setSellerName("AdminTest")
                .setyearCreated(1999)
                .setAuthor("Artist Test");

        String id = itemDAO.addItem(art);
        assertNotNull(id, "DAO phai tra ve id sau khi luu");
        assertTrue(!id.isEmpty(), "DAO phai luu thanh cong ma khong can truyen ID");
    }

    @Test
    void testGetItem_Success() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        String existingIdInDB = "I00005";
        ItemGeneric retrieved = itemDAO.getItem(existingIdInDB);

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
}
