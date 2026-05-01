package ddc.server.model.item;

import java.sql.Connection;
import java.sql.SQLException;
import ddc.server.model.item.ItemGeneric;

import ddc.server.exception.ItemValidationException;
import ddc.server.model.entity.Entity;

/**
 * ItemGeneric đóng vai trò là lớp hỗ trợ Builder (Fluent Interface).
 * T giúp các lớp con (Art, Electronics, Vehicle) trả về đúng kiểu của chúng.p
 */
public abstract class ItemGeneric<T extends ItemGeneric<T>> extends Entity<T> {
    private String itemName;
    private String category;
    private String description;
    private String sellerName;

    public ItemGeneric () {}

    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getSellerName() { return sellerName; }

    public T setItemName (String itemName) {
        this.itemName = itemName;
        return self();
    }

    public T setCategory (String category) {
        this.category = category;
        return self();
    }

    public T setDescription (String description) {
        this.description = description;
        return self();
    }

    public T setSellerName (String sellerName) {
        this.sellerName = sellerName;
        return self();
    }

    public abstract String save(Connection con) throws SQLException;
    public abstract void load(Connection con) throws SQLException;
    
    public void validate() throws ItemValidationException{
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new ItemValidationException("Tên sản phẩm không được để trống.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ItemValidationException("Loại sản phẩm không hợp lệ.");
        }
        if (sellerName == null || sellerName.trim().isEmpty()) {
            throw new ItemValidationException("Tên người bán không được để trống.");
        }
    }
}