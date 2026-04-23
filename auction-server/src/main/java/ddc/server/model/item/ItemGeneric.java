package ddc.server.model.item;

import java.sql.Connection;
import java.sql.SQLException;

import ddc.server.exception.ItemValidationException;
import ddc.server.model.entity.Entity;

/**
 * ItemGeneric đóng vai trò là lớp hỗ trợ Builder (Fluent Interface).
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

<<<<<<< HEAD
    public abstract String save(Connection con) throws SQLException;
=======
    @Override
    public String toString() {
        return String.format("[%s] | Loại: %s | Người bán: %s", 
                      itemName, category, sellerName);
    }

    public void validate() throws ItemValidationException {
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

   public void save(Connection con) throws SQLException {
        String sql = "INSERT INTO ddc_items (item_name, category, description, seller_name) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, itemName);
            pst.setString(2, category);
            pst.setString(3, description);
            pst.setString(4, sellerName);
            pst.executeUpdate();

            // Lấy ID vừa được Trigger tạo ra
            String idMoi = "";
            String sqlGetId = "SELECT id FROM ddc_items WHERE item_name = ? AND seller_name = ? ORDER BY id DESC LIMIT 1";
            try (PreparedStatement pstId = con.prepareStatement(sqlGetId)) {
                pstId.setString(1, itemName);
                pstId.setString(2, sellerName);
                try (ResultSet rs = pstId.executeQuery()) {
                    if (rs.next()) {
                        idMoi = rs.getString("id");
                        saveSpecificDetails(con, idMoi);
                    }
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    


    protected abstract void saveSpecificDetails(Connection con, String idMoi) throws SQLException;
    public abstract void loadSpecificDetails(Connection con) throws SQLException;
>>>>>>> master
}