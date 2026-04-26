
package ddc.server.pattern.factory;

import java.util.HashMap;
import java.util.Map;

import ddc.server.exception.ItemValidationException.InvalidCategoryException;

// Lớp như cái kho chứa các Creators (Để không switch-case)
public class CreatorRegistry {

    private static final Map<String, ItemCreator> creators = new HashMap<>();

    // Khối static này sẽ chạy ngay khi chương trình khởi động để đăng ký các xưởng
    static {
        creators.put("ART", new ArtCreator());
        creators.put("ELECTRONICS", new ElectronicsCreator());
        creators.put("VEHICLE", new VehicleCreator());
        creators.put("GENERAL", new GeneralCreator());
    }

    //Hàm lấy Creator
    public static ItemCreator getCreator(String type) throws InvalidCategoryException {
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidCategoryException("Category khong duoc de trong!");
        }
        
        ItemCreator creator = creators.get(type.toUpperCase());
        
        if (creator == null) {
            throw new InvalidCategoryException("He thong chua ho tro loai: " + type);
        }
        
        return creator;
}
}