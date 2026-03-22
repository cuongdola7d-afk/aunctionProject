package com.aunction.entity.item;

public class Vehicle extends Item {
    private String manufacturer;
    private int year;

    @Override
    public String getCategory() {
        return "Vehicle";
    }
}