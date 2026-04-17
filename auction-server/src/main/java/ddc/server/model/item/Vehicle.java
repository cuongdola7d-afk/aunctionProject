package ddc.server.model.item;

public class Vehicle extends ItemGeneric<Vehicle>{
    private String manufacturer;
    private int year;

    public Vehicle () {}

    //Getters
    public String getManufacturer() { return manufacturer; }
    public int getYear() { return year; }

    //Setters
    public Vehicle setManufacturer (String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public Vehicle setYear (int year) {
        this.year = year;
        return this;
    }
}