package ddc.server.pattern.factory.ItemCreator;

//Class lưu lại thông tin của item để nhét vào constructor -> createItem

public class ItemRequest {
    // Thông tin chung
    public String type; // "ELECTRONICS", "ART", "VEHICLE"
    public String name;
    public double startingPrice;
    public String description;

    // Thông tin riêng cho Electronics
    public String brand;
    public int warrantyMonths;

    // Thông tin riêng cho Art
    public String artist;
    public int yearCreated;

    // Thông tin riêng cho Vehicle
    public String manufacturer;
    public int vehicleYear;
}
