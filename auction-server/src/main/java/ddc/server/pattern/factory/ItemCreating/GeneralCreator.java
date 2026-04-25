package ddc.server.pattern.factory.ItemCreating;

import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.General;
import ddc.server.model.item.ItemGeneric;

public class GeneralCreator extends ItemCreator{
    @Override
    public ItemGeneric createItem(ItemRequest req) throws ItemValidationException {
        return General.create()
                .setItemName(req.itemName)
                .setDescription(req.description)
                .setCategory(req.category)
                .setSellerName(req.sellerName);
    }
}
