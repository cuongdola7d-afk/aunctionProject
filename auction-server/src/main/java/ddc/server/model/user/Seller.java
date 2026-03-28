package ddc.server.model.user;

import java.util.List;

import ddc.server.model.item.*;

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