package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ddc.server.dao.ItemDAO;
import ddc.server.exception.ItemValidationException;
import ddc.server.exception.ItemValidationException.InvalidCategoryException;
import ddc.server.exception.ItemValidationException.MissingFieldException;
import ddc.server.model.item.Art;
import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.ItemRequest;

public class ItemServiceTest {
    private ItemDAO itemDAO;
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemDAO = mock(ItemDAO.class);
        itemService = new ItemService(itemDAO);
    }

    @Test
    void testCreateAndSave_MissingName_ShouldThrowException() {
        ItemRequest req = new ItemRequest()
                .setItemName("")
                .setCategory("ART");

        assertThrows(MissingFieldException.class, () -> itemService.createAndSaveItem(req),
                "Service phai chan lai khi thieu ten san pham");
    }

    @Test
    void testCreateAndSave_UnknownCategory_ShouldThrowException() {
        ItemRequest req = new ItemRequest()
                .setItemName("Do vat la")
                .setCategory("UNKNOWN_CAT");

        assertThrows(InvalidCategoryException.class, () -> itemService.createAndSaveItem(req),
                "Service phai nem loi khi category khong hop le");
    }

    @Test
    void testCreateAndSave_ArtMissingAuthor_ShouldThrowException() {
        ItemRequest req = new ItemRequest()
                .setItemName("Tranh son dau")
                .setCategory("ART")
                .setSellerName("Do Duc Cuong")
                .setAuthor("");

        assertThrows(MissingFieldException.class, () -> itemService.createAndSaveItem(req));
    }

    @Test
    void testCreateAndSave_Success() throws ItemValidationException {
        when(itemDAO.addItem(any(ItemGeneric.class))).thenReturn("I_TEST");

        ItemRequest req = new ItemRequest()
                .setItemName("Binh co trieu dai Thanh")
                .setCategory("ART")
                .setDescription("abc")
                .setAuthor("An danh")
                .setSellerName("cuongdo123")
                .setYearCreated(1991);
        

        String generatedId = itemService.createAndSaveItem(req);
        // 3. Kiểm chứng (Assertions)
        assertEquals("I_TEST", generatedId);
        assertTrue(generatedId.length() > 0, "ID khong duoc de trong");
    }

    @Test
    void testGetItemDetails_Found_ShouldReturnItem() {
        String testId = "I00005";
        Art item = new Art()
                .setItemName("Tranh")
                .setDescription("abc")
                .setSellerName("cuongdo123")
                .setAuthor("An danh")
                .setyearCreated(1991);
        item.setId(testId);
        when(itemDAO.getItem(testId)).thenReturn(item);

        ItemGeneric result = itemService.getItemDetails(testId);

        assertNotNull(result, "Phai tim thay item voi ID hop le");
        assertEquals(testId, result.getId());
    }

    @Test
    void testGetItemDetails_NotFound_ShouldReturnNull() {
        String fakeId = "ID_LINH_TINH";
        when(itemDAO.getItem(fakeId)).thenReturn(null);

        ItemGeneric result = itemService.getItemDetails(fakeId);
        assertNull(result, "Neu khong co ID thi phai tra ve null");
    }
}
