package ddc.server.pattern.factory;

import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ItemCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCreator.class);

    // Factory Method
    // Nó nhận vào "kho dữ liệu" ItemRequest và trả về một Item đã được chuẩn hóa
    public abstract ItemGeneric createItem(ItemRequest req) throws ItemValidationException;

    protected void logCreation(String itemName) {
        LOGGER.info("Đang khởi tạo sản phẩm: {}", itemName);
    }
}