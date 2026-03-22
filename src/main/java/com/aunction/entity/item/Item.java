package entity.item;

import entity.base.BaseEntity;
import entity.user.Seller;

public abstract class Item extends BaseEntity {
    protected String name;
    protected String description;
    protected double startingPrice;
    protected double currentPrice;
    protected Seller seller;

    public abstract String getCategory();

    // getters/setters
}