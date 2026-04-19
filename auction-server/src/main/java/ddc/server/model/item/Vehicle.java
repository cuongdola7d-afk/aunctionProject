package ddc.server.model.item;

public class Vehicle extends Item {
    private String manufacturer;
    private int year;

    public Vehicle() {
        setCategory("VEHICLE");
    }

    public Vehicle(String itemName, String description, double startingPrice) {
        super(itemName, description, startingPrice);
        setCategory("VEHICLE");
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}