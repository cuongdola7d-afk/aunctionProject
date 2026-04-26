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
    public String yearCreated;

    // Thông tin riêng cho Vehicle
    public String manufacturer;
    public int year;

    public ItemRequest() {
    }


    // 2. Constructor đầy đủ (Dùng khi bạn muốn tạo request thủ công trong code test)
    public ItemRequest(String itemName, String description, String category, String sellerName) {
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.sellerName = sellerName;
        
    }

    // Gợi ý: Nếu bạn muốn tạo ArtRequest nhanh
    public static ItemRequest createArtRequest(String itemName, String description, String category, String sellerName, String author, String year) {
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

    // --- FLUENT SETTERS ---
    public ItemRequest setCategory(String category) {
        this.category = category;
        return this;
    }

    public ItemRequest setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public ItemRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public ItemRequest setSellerName(String sellerName) {
        this.sellerName = sellerName;
        return this;
    }

    // Setters cho Electronics
    public ItemRequest setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public ItemRequest setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return this;
    }

    // Setters cho Art
    public ItemRequest setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ItemRequest setYearCreated(String yearCreated) {
        this.yearCreated = yearCreated;
        return this;
    }

    // Setters cho Vehicle
    public ItemRequest setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public ItemRequest setYear(int year) {
        this.year = year;
        return this;
    }

    // --- GETTERS (Để Factory lấy dữ liệu ra) ---
    public String getCategory() { return category; }
    public String getItemName() { return itemName; }
    public String getDescription() { return description; }
    public String getSellerName() { return sellerName; }

    public String getBrand() { return brand; }
    public int getWarrantyMonths() { return warrantyMonths; }

    public String getAuthor() { return author; }
    public String getYearCreated() { return yearCreated; }

    public String getManufacturer() { return manufacturer; }
    public int getYear() { return year; }
}
