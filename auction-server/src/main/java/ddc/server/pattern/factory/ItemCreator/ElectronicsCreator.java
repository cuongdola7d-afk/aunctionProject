package ddc.server.pattern.factory.ItemCreator;

import ddc.server.model.item.*;
import ddc.server.exception.ItemValidationException;

public class ElectronicsCreator extends ItemCreator {
    @Override
    public Item createItem(ItemRequest req) throws ItemValidationException {
        // Sử dụng chuỗi Fluent API, lọc đúng các field của Electronics
        return Electronics.create()
                .setItemName(req.name)
                .setDescription(req.description)
                .setStartingPrice(req.startingPrice)
                .setBrand(req.brand)              // Nhặt brand từ Request
                .setWarrantyMonths(req.warrantyMonths) // Nhặt warranty từ Request
                .validate();                      // Kiểm tra lỗi
    }
}