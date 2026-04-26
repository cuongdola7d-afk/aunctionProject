package ddc.client.model.ItemDTO;

public class VehicleDTO extends ItemGeneric<VehicleDTO>{
    private String manufacturer;
    private int year;

    public VehicleDTO () {}

    public static VehicleDTO create() {
        return new VehicleDTO();
    }

    //Getters
    public String getManufacturer() { return manufacturer; }
    public int getYear() { return year; }

    //Setters
    public VehicleDTO setManufacturer (String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public VehicleDTO setYear (int year) {
        this.year = year;
        return this;
    }
}