package ddc.server.model.user;

import java.util.ArrayList;
import java.util.List;

import ddc.server.model.item.Item;

public class Seller{
    private List<Item> itemForSale = new ArrayList<>();

    public void addItem (Item item) {
        itemForSale.add(item);
    }
}