package ddc.server.pattern.factory.ItemCreator;

import ddc.server.model.item.*;
import ddc.server.exception.ItemValidationException;

public class VehicleCreator extends ItemCreator {
    @Override
    public Item createItem(ItemRequest req) throws ItemValidationException {
        // Nhặt đúng manufacturer và vehicleYear từ Request cồng kềnh
        return Vehicle.create()
                .setItemName(req.name)
                .setDescription(req.description)
                .setStartingPrice(req.startingPrice)
                .setManufacturer(req.manufacturer)
                .setYear(req.vehicleYear)
                .validate();
    }
}