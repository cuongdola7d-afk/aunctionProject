package ddc.server.model.item;

import ddc.server.model.entity.Entity;

public abstract class Item extends Entity {
    private String itemName;
    private String description;
    private String category;
    private String sellerName;
    private double startingPrice;
    private double currentPrice;

    public Item() {
    }

    public Item(String itemName, String description, double startingPrice) {
        this.itemName = itemName;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
    }

    public String getName() {
        return itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}