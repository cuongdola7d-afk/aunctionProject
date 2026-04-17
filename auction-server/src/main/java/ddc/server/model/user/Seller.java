package ddc.server.model.user;

import java.util.ArrayList;
import java.util.List;

import ddc.server.model.item.ItemGeneric;

public class Seller{
    private List<ItemGeneric> itemForSale = new ArrayList<>();

    public void addItem (ItemGeneric item) {
        itemForSale.add(item);
    }
}