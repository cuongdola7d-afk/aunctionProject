package ddc.client.model.ItemDTO.factory;

import ddc.client.exception.ItemValidationException;
import ddc.client.model.ItemDTO.ItemGeneric;

public abstract class ItemCreator {

    // Factory Method
    // Nó nhận vào "kho dữ liệu" ItemRequest và trả về một Item đã được chuẩn hóa
    public abstract ItemGeneric createItem(ItemRequest req) throws ItemValidationException;

    protected void logCreation(String itemName) {
        System.out.println("[Factory] Đang khởi tạo sản phẩm: " + itemName);
    }
}