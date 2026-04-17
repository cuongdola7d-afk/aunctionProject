package ddc.server.pattern.factory.ItemCreator;

import ddc.server.model.item.ItemGeneric;

public abstract class ItemCreator {
    public abstract ItemGeneric createItem(ItemRequest req);
}
