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

    public String getDescription() {
        return description;
    }

    public String getSellerName() {
        return sellerName;
    }

    public double getStartingPrice() {
        return startingPrice;
    }


    public double getCurrentPrice() {
        return currentPrice;
    }


    public String getCategory() {
        return category;
    }


    public Item setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public Item setDescription(String description) {
        this.description = description;
        return this;
    }

    public Item setCategory(String category) {
        this.category = category;
        return this;
    }

    public Item setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
        return this;
    }
    public Item setCurrentPrice(double currentPricePrice) {
            this.currentPrice = currentPrice;
            return this;
        }

    public Item setSellerName(String sellerName){
        this.sellerName = sellerName;
        return this;
    }
}