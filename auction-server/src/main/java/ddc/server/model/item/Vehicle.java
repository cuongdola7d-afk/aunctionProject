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
    public String toString() {
        return super.toString() + String.format(" | Brand: %s | Nam SX: %d", manufacturer , year);
    }

 @Override
    protected void saveSpecificDetails(Connection con, String itemId) throws SQLException {
        String sql = "INSERT INTO item_vehicle (id, manufacterer, year) VALUES (?, ?, ?)";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, itemId);          // Dùng itemId nhận được từ cha
            pst.setString(2, this.manufacturer);       
            pst.setInt(3, this.year); 
            
            pst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    @Override
    public void loadSpecificDetails(Connection con) throws SQLException {
        String sql = "SELECT manufacturer, year FROM item_vehicle WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, this.getId()); // Lấy ID của chính món đồ này
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    this.manufacturer = rs.getString("manufacturer");
                    this.year = rs.getInt("year");
                }
            }
        }
    }

    // Chốt chặn Validation cho phương tiện
    public void validate() throws ItemValidationException {
        super.validate();

        if (manufacturer == null || manufacturer.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu thông tin nhà sản xuất");
            
        if (year < 1886) // Năm chiếc ô tô đầu tiên ra đời
            throw new ItemValidationException.InvalidValueException("Năm sản xuất không hợp lệ");
    }
}