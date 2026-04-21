package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ddc.server.exception.ItemValidationException;

public class Electronics extends ItemGeneric<Electronics> {
    private String brand;
    private int warrantyMonths;

    public Electronics() {
        setCategory("ELECTRONICS");
    }

    // Static Factory Method để không dùng 'new' ở ngoài
    public static Electronics create() {
        return new Electronics();
    }

    //Getters
    public String getBrand() { return brand; }
    public int getWarrantyMonths() { return warrantyMonths; }

    //Setters
    public Electronics setBrand (String brand) {
        this.brand = brand;
        return this;
    }
    
    public Electronics setWarrantyMonths (int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return this;
    }

    @Override
    protected void saveSpecificDetails(Connection con, String itemId) throws SQLException {
    String sql = "INSERT INTO item_electronics (id, brand, warranty_months) VALUES (?, ?, ?)";
    
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, itemId);          // Dùng itemId nhận được từ cha
        pst.setString(2, this.brand);       
        pst.setInt(3, this.warrantyMonths); 
        
        pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Chốt chặn Validation
    public Electronics validate() throws ItemValidationException {
        if (getItemName() == null || getItemName().isEmpty()) 
            throw new ItemValidationException.MissingFieldException("Tên thiết kế bị trống");
        
        // if (getStartingPrice() <= 0)
        //     throw new ItemValidationException.InvalidPriceException("Giá khởi điểm điện tử phải > 0");

        if (brand == null || brand.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu thương hiệu (Brand)");
            
        if (warrantyMonths < 0)
            throw new ItemValidationException.InvalidPriceException("Thời gian bảo hành không hợp lệ");

        return this;
    }
}