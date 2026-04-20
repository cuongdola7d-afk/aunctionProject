package ddc.server.pattern.factory.ItemCreating;

import java.util.HashMap;
import java.util.Map;

// Lớp như cái kho chứa các Creators (Để không switch-case)
public class CreatorRegistry {

    private static final Map<String, ItemCreator> creators = new HashMap<>();

    // Khối static này sẽ chạy ngay khi chương trình khởi động để đăng ký các xưởng
    static {
        creators.put("ART", new ArtCreator());
        creators.put("ELECTRONICS", new ElectronicsCreator());
        creators.put("VEHICLE", new VehicleCreator());
    }

    //Hàm lấy Creator
    public static ItemCreator getCreator(String type) {
        if (type == null) return null;
        return creators.get(type.toUpperCase());
    }
}