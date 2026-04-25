package ddc.client.model.ItemDTO.factory;

import ddc.client.exception.ItemValidationException;
import ddc.client.model.ItemDTO.GeneralDTO;
import ddc.client.model.ItemDTO.ItemGeneric;

public class GeneralCreator extends ItemCreator{
    @Override
    public ItemGeneric createItem(ItemRequest req) throws ItemValidationException {
        return GeneralDTO.create()
                .setId(req.id)
                .setItemName(req.itemName)
                .setDescription(req.description)
                .setCategory(req.category)
                .setSellerName(req.sellerName);
    }
}
