package com.auction.dao;

import com.auction.entity.item.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getAllItems() {
        return items;
    }

    public void deleteItem(String id) {
        items.removeIf(item -> item.getId().equals(id));
    }
}