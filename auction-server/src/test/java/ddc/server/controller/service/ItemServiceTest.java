package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.ItemCreating.ItemRequest;
import ddc.server.exception.ItemValidationException;
import ddc.server.exception.ItemValidationException.InvalidCategoryException;
import ddc.server.exception.ItemValidationException.MissingFieldException;

public class ItemServiceTest {
    private static ItemService itemService;

    @BeforeAll
    static void initAll() {
        itemService = new ItemService();
    }

    // 1. TEST THIEU TEN (THUOC TINH CHUNG)
    @Test
    void testCreateAndSave_MissingName_ShouldThrowException() {
        ItemRequest req = new ItemRequest()
                .setItemName("") // Ten trong
                .setCategory("ART");

        assertThrows(MissingFieldException.class, () -> {
            itemService.createAndSaveItem(req);
        }, "Service phai chan lai khi thieu ten san pham");
    }

    // 2. TEST CATEGORY KHONG TON TAI (PHOI HOP VOI REGISTRY)
    @Test
    void testCreateAndSave_UnknownCategory_ShouldThrowException() {
        ItemRequest req = new ItemRequest()
                .setItemName("Do vat la")
                .setCategory("UNKNOWN_CAT");

        assertThrows(InvalidCategoryException.class, () -> {
            itemService.createAndSaveItem(req);
        }, "Service phai nem loi khi category khong hop le");
    }

    // 3. TEST THIEU THUOC TINH RIENG
    @Test
    void testCreateAndSave_ArtMissingAuthor_ShouldThrowException() {
        ItemRequest req = new ItemRequest()
                .setItemName("Tranh son dau")
                .setCategory("ART")
                .setSellerName("Do Duc Cuong")
                .setAuthor("");

        assertThrows(MissingFieldException.class, () -> {
            itemService.createAndSaveItem(req);
        });
    }

    // 4. TEST LUU THANH CONG
    @Test
    void testCreateAndSave_Success() throws ItemValidationException {
        ItemRequest req = new ItemRequest()
                .setItemName("Binh co trieu dai Thanh")
                .setCategory("ART")
                .setAuthor("An danh")
                .setSellerName("Nguyen Van A")
                .setYearCreated("1991");
        boolean result = itemService.createAndSaveItem(req);
        
        assertTrue(result, "Service phai tra ve true khi luu thanh cong");
    }

    //5. TEST TRA VE ITEM DUNG
    @Test
    void testGetItemDetails_Found_ShouldReturnItem() {
        String testId = "I00005";
        
        ItemGeneric result = itemService.getItemDetails(testId);
        
        assertNotNull(result, "Phai tim thay item voi ID hop le");
        assertEquals(testId, result.getId());
    }
    
    //6. TEST TRA VE SAI
    @Test
    void testGetItemDetails_NotFound_ShouldReturnNull() {
        String fakeId = "ID_LINH_TINH";
        
        ItemGeneric result = itemService.getItemDetails(fakeId);        
        assertNull(result, "Neu khong co ID thi phai tra ve null");
    }
}