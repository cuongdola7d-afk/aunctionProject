package ddc.server.model.item;

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

    // Fluent Setters riêng cho Vehicle
    public Vehicle setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return self();
    }

    public Vehicle setYear(int year) {
        this.year = year;
        return self();
    }

    // Chốt chặn Validation cho phương tiện
    public Vehicle validate() throws ItemValidationException {
        if (getItemName() == null || getItemName().isEmpty()) 
            throw new ItemValidationException.MissingFieldException("Tên phương tiện không được để trống");
        
        if (getStartingPrice() <= 0)
            throw new ItemValidationException.InvalidPriceException("Giá khởi điểm phải lớn hơn 0");

        if (manufacturer == null || manufacturer.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu thông tin nhà sản xuất");
            
        if (year < 1886) // Năm chiếc ô tô đầu tiên ra đời
            throw new ItemValidationException.InvalidPriceException("Năm sản xuất không hợp lệ");

        return this;
    }
}