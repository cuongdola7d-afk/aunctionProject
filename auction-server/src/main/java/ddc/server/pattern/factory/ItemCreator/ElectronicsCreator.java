package ddc.server.pattern.factory.ItemCreator;

import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.Electronics;
import ddc.server.model.item.ItemGeneric;

public class ElectronicsCreator extends ItemCreator {
    @Override
    public ItemGeneric createItem(ItemRequest req) throws ItemValidationException {
        // Sử dụng chuỗi Fluent API, lọc đúng các field của Electronics
        return Electronics.create()
                .setItemName(req.name)
                .setDescription(req.description)
                .setBrand(req.brand)              // Nhặt brand từ Request
                .setWarrantyMonths(req.warrantyMonths) // Nhặt warranty từ Request
                .validate();                      // Kiểm tra lỗi
    }
}