
package ddc.server.pattern.factory;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ddc.server.exception.ItemValidationException.InvalidCategoryException;

public class CreatorRegistryTest {

    // 1. TEST TRUONG HOP HOP LE (ART)
    @Test
    void testGetCreator_Art_Success() throws InvalidCategoryException {
        ItemCreator creator = CreatorRegistry.getCreator("ART");
        
        assertNotNull(creator, "Creator không duoc null");
        assertTrue(creator instanceof ArtCreator, "Phai tra ve dung kieu ArtCreator");
    }

    // 2. TEST TRUONG HOP HOP LE (VEHICLE - viet thuong van phai nhan)
    @Test
    void testGetCreator_Vehicle_LowerCase_Success() throws InvalidCategoryException {
        ItemCreator creator = CreatorRegistry.getCreator("vehicle");
        
        assertNotNull(creator);
        assertTrue(creator instanceof VehicleCreator, "Phai nhan dien duoc ca chu thuong");
    }

    // 3. TEST TRUONG HOP CATEGORY TRONG (NULL)
    @Test
    void testGetCreator_NullType_ShouldThrowException() {
        assertThrows(InvalidCategoryException.class, () -> {
            CreatorRegistry.getCreator(null);
        }, "Phai nem loi khi category la null");
    }

    // 4. TEST TRUONG HOP CATEGORY RONG ("")
    @Test
    void testGetCreator_EmptyType_ShouldThrowException() {
        assertThrows(InvalidCategoryException.class, () -> {
            CreatorRegistry.getCreator("   ");
        }, "Phai nem loi khi category chi co khoang trang");
    }

    // 5. TEST TRUONG HOP LOAI KHONG TON TAI
    @Test
    void testGetCreator_UnknownType_ShouldThrowException() {
        String unknownType = "DO_VAT_LA";
        
        InvalidCategoryException exception = assertThrows(InvalidCategoryException.class, () -> {
            CreatorRegistry.getCreator(unknownType);
        });

        // Kiem tra xem message tra ve co dung nhu minh set khong
        assertTrue(exception.getMessage().contains("He thong chua ho tro loai"));
    }
}