package ddc.server.pattern.factory;


import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;

public abstract class ItemCreator {

    // Factory Method
    // Nó nhận vào "kho dữ liệu" ItemRequest và trả về một Item đã được chuẩn hóa
    public abstract ItemGeneric createItem(ItemRequest req) throws ItemValidationException;

    protected void logCreation(String itemName) {
        System.out.println("[Factory] Đang khởi tạo sản phẩm: " + itemName);
    }
}