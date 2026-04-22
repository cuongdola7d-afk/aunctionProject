package ddc.server.pattern.factory.ItemCreating;
//Class lưu lại thông tin của item để nhét vào constructor -> createItem

public class ItemRequest {
    // Thông tin chung
    public String category;
    public String itemName;
    public String description;
    public String sellerName;

    // Thông tin riêng cho Electronics
    public String brand;
    public int warrantyMonths;

    // Thông tin riêng cho Art
    public String author;
    public int yearCreated;

    // Thông tin riêng cho Vehicle
    public String manufacturer;
    public int year;

    public ItemRequest() {
    }

    public String getCategory(){
        return this.category;
    }
    public String getItemName(){
        return this.itemName;
    }
    // 2. Constructor đầy đủ (Dùng khi bạn muốn tạo request thủ công trong code test)
    public ItemRequest(String itemName, String description, String category, String sellerName) {
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.sellerName = sellerName;
        
    }

    // Gợi ý: Nếu bạn muốn tạo ArtRequest nhanh
    public static ItemRequest createArtRequest(String itemName, String description, String category, String sellerName, String author, int year) {
        ItemRequest req = new ItemRequest(itemName,description,category,sellerName);
        req.author = author;
        req.yearCreated = year;
        return req;
    }
    
    public static ItemRequest createElectronicsRequest(String itemName, String description, String category, String sellerName, String brand, int warrentyMonths) {
        ItemRequest req = new ItemRequest(itemName,description,category,sellerName);
        req.brand = brand;
        req.warrantyMonths = warrentyMonths;
        return req;
    }

    public static ItemRequest createVehicleRequest(String itemName, String description, String category, String sellerName, String manufacturer, int year) {
        ItemRequest req = new ItemRequest(itemName,description,category,sellerName);
        req.manufacturer = manufacturer;
        req.year = year;
        return req;
    }
}
