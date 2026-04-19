package ddc.server.model.item;

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

    // Fluent Setters riêng cho Electronics
    public Electronics setBrand(String brand) {
        this.brand = brand;
        return self();
    }

    public Electronics setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return self();
    }

    // Chốt chặn Validation
    public Electronics validate() throws ItemValidationException {
        if (getItemName() == null || getItemName().isEmpty()) 
            throw new ItemValidationException.MissingFieldException("Tên thiết kế bị trống");
        
        if (getStartingPrice() <= 0)
            throw new ItemValidationException.InvalidPriceException("Giá khởi điểm điện tử phải > 0");

        if (brand == null || brand.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu thương hiệu (Brand)");
            
        if (warrantyMonths < 0)
            throw new ItemValidationException.InvalidPriceException("Thời gian bảo hành không hợp lệ");

        return this;
    }
}