package ddc.server.model.user;
import java.util.List;

import ddc.server.model.item.Item;
public class Seller {
    private List<Item> itemsForSale;

    public void addItem(Item item) {
        itemsForSale.add(item);
    }

    public void removeItem(Item item) {
        itemsForSale.remove(item);
    }
}