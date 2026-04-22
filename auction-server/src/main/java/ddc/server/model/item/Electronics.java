package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public String save(Connection con) throws SQLException {
        String sqlInsert = "CALL insert_electronics (?, ?, ?, ?, ?, ?)";

        String sqlGetId = "SELECT @item_id AS generated_id;";
        
        try (PreparedStatement pst1 = con.prepareStatement(sqlInsert)) {
            pst1.setString(1, getItemName());
            pst1.setString(2, getCategory());
            pst1.setString(3, getDescription());
            pst1.setString(4, getSellerName());
            pst1.setString(5, brand);
            pst1.setInt(6, warrantyMonths);
            
            int rowExecuted = pst1.executeUpdate();

            if (rowExecuted > 0) {
                try (PreparedStatement pst2 = con.prepareStatement(sqlGetId);
                    ResultSet rs = pst2.executeQuery()) {
                    if (rs.next()) {
                        String id = rs.getString("generated_id");
                        return id;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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