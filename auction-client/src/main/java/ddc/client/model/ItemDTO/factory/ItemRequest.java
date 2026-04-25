package ddc.client.model.ItemDTO.factory;
//Class lưu lại thông tin của item để nhét vào constructor -> createItem

public class ItemRequest {
    public String id;
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
    public ItemRequest(String id, String itemName, String description, String category, String sellerName) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.sellerName = sellerName;
        
    }
}
