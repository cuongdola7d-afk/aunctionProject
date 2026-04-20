package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ddc.server.model.entity.Entity;

/**
 * ItemGeneric đóng vai trò là lớp hỗ trợ Builder (Fluent Interface).
 * Nó không giữ biến mà dùng 'super' để đổ dữ liệu vào lớp Item cha.
 * T giúp các lớp con (Art, Electronics, Vehicle) trả về đúng kiểu của chúng.
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

    public void save (Connection con) {
        String sql = "INSERT INTO ddc_items (item_name, category, description, seller_name) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, itemName);
            pst.setString(2, category);
            pst.setString(3, description);
            pst.setString(4, sellerName);
            
            pst.executeUpdate();
            
            saveSpecificDetails(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract void saveSpecificDetails(Connection con) throws SQLException;
}