package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ddc.server.exception.ItemValidationException;
import ddc.server.exception.ItemValidationException.InvalidCategoryException;
import ddc.server.exception.ItemValidationException.MissingFieldException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.ItemRequest;

public class ItemServiceTest {
    private static ItemService itemService;

    @BeforeAll
    static void initAll() {
        itemService = new ItemService();
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
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        ItemRequest req = new ItemRequest()
                .setItemName("Binh co trieu dai Thanh")
                .setCategory("ART")
                .setAuthor("An danh")
                .setSellerName("Nguyen Van A")
                .setYearCreated(1991);

        String id = itemService.createAndSaveItem(req);
        assertNotNull(id, "Service phai tra ve id khi luu thanh cong");
        assertTrue(!id.isEmpty(), "Service phai tra ve id khi luu thanh cong");
    }

    @Test
    void testGetItemDetails_Found_ShouldReturnItem() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        String testId = "I00005";
        ItemGeneric result = itemService.getItemDetails(testId);

        assertNotNull(result, "Phai tim thay item voi ID hop le");
        assertEquals(testId, result.getId());
    }

    @Test
    void testGetItemDetails_NotFound_ShouldReturnNull() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        String fakeId = "ID_LINH_TINH";
        ItemGeneric result = itemService.getItemDetails(fakeId);
        assertNull(result, "Neu khong co ID thi phai tra ve null");
    }

    private boolean hasDbConfig() {
        return hasValue("DDC_DB_URL") && hasValue("DDC_DB_USER") && hasValue("DDC_DB_PASSWORD");
    }

    private boolean hasValue(String envName) {
        String value = System.getenv(envName);
        return value != null && !value.isBlank();
    }
}
