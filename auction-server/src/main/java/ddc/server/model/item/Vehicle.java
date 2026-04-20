package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    public void saveSpecificDetails (Connection con) {
        String sql = "INSERT INTO item_art (manufacturer, year) VALUES (?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, manufacturer);
            pst.setInt(2, year);

            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Chốt chặn Validation cho phương tiện
    public Vehicle validate() throws ItemValidationException {
        if (getItemName() == null || getItemName().isEmpty()) 
            throw new ItemValidationException.MissingFieldException("Tên phương tiện không được để trống");
        
        // if (getStartingPrice() <= 0)
        //     throw new ItemValidationException.InvalidPriceException("Giá khởi điểm phải lớn hơn 0");

        if (manufacturer == null || manufacturer.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu thông tin nhà sản xuất");
            
        if (year < 1886) // Năm chiếc ô tô đầu tiên ra đời
            throw new ItemValidationException.InvalidPriceException("Năm sản xuất không hợp lệ");

        return this;
    }
}