package com.auction.dao;

import com.auction.entity.item.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    private static ItemDAO instance;
    private List<Item> items;

    private ItemDAO() {
        items = new ArrayList<>();
    }

    public static ItemDAO getInstance() {
        if (instance == null) {
            instance = new ItemDAO();
        }
        return instance;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getAllItems() {
        return items;
    }

    public Item getItemById(String id) {
        return items.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void updateItem(Item updatedItem) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == updatedItem.getId()) {
                items.set(i, updatedItem);
                return;
            }
        }
    }

    public void deleteItem(int id) {
        items.removeIf(i -> i.getId().equals(id));
    }
}