package ddc.client.model.ItemDTO.factory;

import ddc.client.exception.ItemValidationException;
import ddc.client.model.ItemDTO.ElectronicsDTO;
import ddc.client.model.ItemDTO.ItemGeneric;

public class ElectronicsCreator extends ItemCreator {
    @Override
    public ItemGeneric createItem(ItemRequest req) throws ItemValidationException {
        // Sử dụng chuỗi Fluent API, lọc đúng các field của Electronics
        return ElectronicsDTO.create()
                .setId(req.id)
                .setItemName(req.itemName)
                .setDescription(req.description)
                .setCategory(req.category)
                .setSellerName(req.sellerName)
                .setBrand(req.brand)              // Nhặt brand từ Request
                .setWarrantyMonths(req.warrantyMonths); 
    }
}