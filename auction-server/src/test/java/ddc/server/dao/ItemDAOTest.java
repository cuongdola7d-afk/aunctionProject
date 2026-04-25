package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ddc.server.model.item.Art;
import ddc.server.model.item.ItemGeneric;

public class ItemDAOTest {
    private ItemDAO itemDAO = new ItemDAO(); 

    // 1: Chỉ test chức năng ADD
    @Test
    void testAddItem_Success() {
        Art art = new Art()
                    .setItemName("Tranh Test ID Tự Động")
                    .setSellerName("AdminTest")
                    .setyearCreated(1999)
                    .setAuthor("Artist Test");

        boolean isSaved = !itemDAO.addItem(art).isEmpty();
        assertTrue(isSaved, "DAO phai luu thanh cong ma khong can truyen ID");
    }

    // 2: Chỉ test chức năng GET
    @Test
    void testGetItem_Success() {
        String existingIdInDB = "I00005"; 
        ItemGeneric retrieved = itemDAO.getItem(existingIdInDB);
        
        assertNotNull(retrieved, "Phai lay ra duoc item co san trong DB");
        assertEquals(existingIdInDB, retrieved.getId());
    }
}