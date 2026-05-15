package ddc.client.model.ItemDTO.factory;

import ddc.client.exception.ItemValidationException;
import ddc.client.model.ItemDTO.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemFactoryTest {

    @Test
    @DisplayName("Test Registry: Phải lấy được đúng Creator từ chuỗi Category")
    void testCreatorRegistry() {
        // Test khớp với CreatorRegistry.java của bạn
        assertTrue(CreatorRegistry.getCreator("ART") instanceof ArtCreator);
        assertTrue(CreatorRegistry.getCreator("ELECTRONICS") instanceof ElectronicsCreator);
        assertTrue(CreatorRegistry.getCreator("VEHICLE") instanceof VehicleCreator);
        assertTrue(CreatorRegistry.getCreator("GENERAL") instanceof GeneralCreator);
    }

    @Test
    @DisplayName("Test Electronics Mapping: Kiểm tra các trường public của ItemRequest")
    void testElectronicsMapping() throws ItemValidationException {
        // Tạo request bằng Constructor chung
        ItemRequest req = new ItemRequest("123", "Laptop Dell", "Máy cũ", "ELECTRONICS", "seller01");
        
        // Gán giá trị trực tiếp vào các public field đặc thù
        req.brand = "Dell";
        req.warrantyMonths = 12;

        // Chạy qua Factory
        ItemCreator creator = CreatorRegistry.getCreator("ELECTRONICS");
        ItemGeneric item = creator.createItem(req);

        // Assert: Kiểm tra đúng loại DTO và đúng dữ liệu
        assertTrue(item instanceof ElectronicsDTO);
        ElectronicsDTO dto = (ElectronicsDTO) item;
        
        assertEquals("Dell", dto.getBrand());
        assertEquals(12, dto.getWarrantyMonths());
        assertEquals("Laptop Dell", dto.getItemName());
    }

    @Test
    @DisplayName("Test Art Mapping: Kiểm tra các trường author/yearCreated")
    void testArtMapping() throws ItemValidationException {
        ItemRequest req = new ItemRequest("456", "Tranh Sen", "Sơn dầu", "ART", "artist01");
        
        // Gán trực tiếp vào public field
        req.author = "Nguyễn Nam";
        req.yearCreated = 2020;

        ItemCreator creator = CreatorRegistry.getCreator("ART");
        ItemGeneric item = creator.createItem(req);

        assertTrue(item instanceof ArtDTO);
        ArtDTO dto = (ArtDTO) item;
        assertEquals("Nguyễn Nam", dto.getAuthor());
        assertEquals(2020, dto.getyearCreated());
    }

    @Test
    @DisplayName("Test Biên: Category null hoặc sai định dạng")
    void testInvalidCategory() {
        // Registry của bạn có check null
        assertNull(CreatorRegistry.getCreator(null));
        
        // Registry dùng toUpperCase() nên "art" vẫn phải ra đúng Creator
        assertNotNull(CreatorRegistry.getCreator("art"));
        
        // Category không tồn tại
        assertNull(CreatorRegistry.getCreator("UNKNOWN_CATEGORY"));
    }
}