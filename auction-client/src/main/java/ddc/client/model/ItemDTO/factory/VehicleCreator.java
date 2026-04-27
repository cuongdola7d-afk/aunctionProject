package ddc.client.model.ItemDTO.factory;

import ddc.client.exception.ItemValidationException;
import ddc.client.model.ItemDTO.ItemGeneric;
import ddc.client.model.ItemDTO.VehicleDTO;

public class VehicleCreator extends ItemCreator {
    @Override
    public ItemGeneric createItem(ItemRequest req) throws ItemValidationException {
        // Nhặt đúng manufacturer và vehicleYear từ Request cồng kềnh
        return VehicleDTO.create()
                .setId(req.id)
                .setItemName(req.itemName)
                .setDescription(req.description)
                .setCategory(req.category)
                .setSellerName(req.sellerName)
                .setManufacturer(req.manufacturer)
                .setYear(req.year);
    }
}