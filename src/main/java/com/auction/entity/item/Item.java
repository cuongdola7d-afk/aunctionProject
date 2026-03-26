package com.aunction.entity.item;

import com.aunction.entity.base.BaseEntity;
import com.aunction.entity.user.Seller;

public abstract class Item extends BaseEntity {
    protected String name;
    protected String description;
    protected double startingPrice;
    protected double currentPrice;
    protected Seller seller;
    
    public Item(String name, String description, double startingPrice){
        super();
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
    }

    public abstract String getCategory();

}