package com.auction.entity.user;

import java.util.List;

import com.auction.entity.item.Item;

public class Seller extends User {
    private List<Item> itemsForSale;

    public void addItem(Item item) {
        itemsForSale.add(item);
    }

    public void removeItem(Item item) {
        itemsForSale.remove(item);
    }

    @Override
    public void printInfo() {
        System.out.println("Seller: " + name);
    }
}