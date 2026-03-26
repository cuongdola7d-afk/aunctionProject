package com.auction.entity.item;

import com.auction.entity.base.BaseEntity;
import com.auction.entity.user.Seller;

public abstract class Item extends BaseEntity {
    protected String name;
    protected String description;
    protected double startingPrice;
    protected double currentPrice;
    protected Seller seller;

    public abstract String getCategory();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}