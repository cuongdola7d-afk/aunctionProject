package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
                    .setSellerName("cuongdo123")
                    .setyearCreated(1999)
                    .setDescription("abc")
                    .setAuthor("Artist Test");

        String generatedId = itemDAO.addItem(art); 
        assertNotNull(generatedId, "DAO phai tra ve ID ");
        assertFalse(generatedId.trim().isEmpty(), "ID tra ve khong duoc rong");
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