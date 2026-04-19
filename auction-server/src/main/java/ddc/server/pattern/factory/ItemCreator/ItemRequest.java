package ddc.server.pattern.factory.ItemCreator;

import java.util.Locale.Category;

//Class lưu lại thông tin của item để nhét vào constructor -> createItem

public class ItemRequest {
    // Thông tin chung
    public String type;
    public String name;
    public double startingPrice;
    public String description;

    // Thông tin riêng cho Electronics
    public String brand;
    public int warrantyMonths;

    // Thông tin riêng cho Art
    public String artist;
    public String yearCreated;

    // Thông tin riêng cho Vehicle
    public String manufacturer;
    public int vehicleYear;

    public ItemRequest() {
    }

    public String getType(){
        return this.type;
    }
    public String getName(){
        return this.name;
    }

    // 2. Constructor đầy đủ (Dùng khi bạn muốn tạo request thủ công trong code test)
    public ItemRequest(String type, String name, double startingPrice, String description) {
        this.type = type;
        this.name = name;
        this.startingPrice = startingPrice;
        this.description = description;
    }

    // Gợi ý: Nếu bạn muốn tạo ArtRequest nhanh
    public static ItemRequest createArtRequest(String name, double price, String artist, String year) {
        ItemRequest req = new ItemRequest("ART", name, price, "");
        req.artist = artist;
        req.yearCreated = year;
        return req;
    }
    
    public static ItemRequest createElectronicsRequest(String name, double price, String brand, int warrentyMonth) {
        ItemRequest req = new ItemRequest("ELECTRONICS", name, price, "");
        req.brand = brand;
        req.warrantyMonths = warrentyMonth;
        return req;
    }

    public static ItemRequest createVehicleRequest(String name, double price, String manufacturer, int vehicleYear) {
        ItemRequest req = new ItemRequest("ART", name, price, "");
        req.manufacturer = manufacturer;
        req.vehicleYear = vehicleYear;
        return req;
    }
}
