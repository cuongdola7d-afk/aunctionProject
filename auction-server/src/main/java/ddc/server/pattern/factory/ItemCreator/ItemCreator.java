package ddc.server.pattern.factory.ItemCreator;

import ddc.server.model.item.Item;

public abstract class ItemCreator {
    public abstract Item createItem(ItemRequest req);
}
