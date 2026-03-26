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

}
