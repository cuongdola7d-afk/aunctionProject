package ddc.server.model.user;

import java.util.ArrayList;
import java.util.List;

import ddc.server.model.item.ItemGeneric;

public class Seller extends User{
    private final List<ItemGeneric> itemForSale = new ArrayList<>();

    public List<ItemGeneric> getItemForSale () { return itemForSale; }

    public void addItem (ItemGeneric item) {
        itemForSale.add(item);
    }
}