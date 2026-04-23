package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ddc.server.exception.ItemValidationException;

public class Vehicle extends ItemGeneric<Vehicle> {
    private String manufacturer;
    private int year;

    public Vehicle() {
        setCategory("VEHICLE");
    }

    // Static Factory Method
    public static Vehicle create() {
        return new Vehicle();
    }

    //Getters
    public String getManufacturer() { return manufacturer; }
    public int getYear() { return year; }

    //Setters
    public Vehicle setManufacturer (String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public Vehicle setYear (int year) {
        this.year = year;
        return this;
    }

    @Override
    public String save(Connection con) throws SQLException {
        String sqlInsert = "CALL insert_vehicle (?, ?, ?, ?, ?, ?)";

        String sqlGetId = "SELECT @item_id AS generated_id;";
        
        try (PreparedStatement pst1 = con.prepareStatement(sqlInsert)) {
            pst1.setString(1, getItemName());
            pst1.setString(2, getCategory());
            pst1.setString(3, getDescription());
            pst1.setString(4, getSellerName());
            pst1.setString(5, manufacturer);
            pst1.setInt(6, year);
            
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

    // Chốt chặn Validation cho phương tiện
    public Vehicle validate() throws ItemValidationException {
        if (getItemName() == null || getItemName().isEmpty()) 
            throw new ItemValidationException.MissingFieldException("Tên phương tiện không được để trống");
        if (manufacturer == null || manufacturer.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu thông tin nhà sản xuất");
        if (year < 1886) // Năm chiếc ô tô đầu tiên ra đời
            throw new ItemValidationException.InvalidPriceException("Năm sản xuất không hợp lệ");
        return this;
    }
}