package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.exception.ItemValidationException;

public class Vehicle extends ItemGeneric<Vehicle> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Vehicle.class);
    private String manufacturer;
    private int year;

    public Vehicle() {
        setCategory("VEHICLE");
    }

    // Static Factory Method
    public static Vehicle create() {
        return new Vehicle();
    }

    // Getters
    public String getManufacturer() {
        return manufacturer;
    }

    public int getYear() {
        return year;
    }

    // Setters
    public Vehicle setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public Vehicle setYear(int year) {
        this.year = year;
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Brand: %s | Nam SX: %d", manufacturer, year);
    }

    @Override
    public String save(Connection con) throws SQLException {
        String sqlInsert = "CALL insert_vehicle (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst1 = con.prepareStatement(sqlInsert)) {
            pst1.setString(1, getItemName());
            pst1.setString(2, getCategory());
            pst1.setString(3, getDescription());
            pst1.setString(4, getSellerName());
            pst1.setString(5, manufacturer);
            pst1.setInt(6, year);
            pst1.setString(7, getImageUrl());

            pst1.executeUpdate();

            String sqlGetId = "SELECT @item_id AS generated_id;";
            try (PreparedStatement pst2 = con.prepareStatement(sqlGetId);
                    ResultSet rs = pst2.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("generated_id");
                    return id;
                }
            }
            return null;
        } catch (SQLException e) {
            LOGGER.error("Loi luu Vehicle", e);
            return null;
        }
    }

    @Override
    public void load(Connection con) throws SQLException {
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