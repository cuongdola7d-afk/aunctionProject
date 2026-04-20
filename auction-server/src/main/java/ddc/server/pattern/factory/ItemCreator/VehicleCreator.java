package ddc.server.pattern.factory.ItemCreator;

import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.model.item.Vehicle;

public class VehicleCreator extends ItemCreator {
    @Override
    public ItemGeneric createItem(ItemRequest req) throws ItemValidationException {
        // Nhặt đúng manufacturer và vehicleYear từ Request cồng kềnh
        return Vehicle.create()
                .setItemName(req.name)
                .setDescription(req.description)
                .setManufacturer(req.manufacturer)
                .setYear(req.vehicleYear)
                .validate();
    }
}