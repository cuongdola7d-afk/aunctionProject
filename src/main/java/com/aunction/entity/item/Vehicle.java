package com.aunction.entity.item;

public class Vehicle extends Item {
    private String manufacturer;
    private int year;

    public Vehicle(String manufacturer, int year){
        this.manufacturer = manufacturer;
        this.year = year;
    }

    @Override
    public String getCategory() {
        return "Vehicle";
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