package ddc.server.model.item;

import ddc.server.exception.ItemValidationException;

// Kế thừa ItemGeneric và truyền chính nó vào Generic T
public class Art extends ItemGeneric<Art> {
    private String author;
    private String yearCreated;

    public Art() {
        setCategory("ART");
    }

    // Static Factory Method: Để Creator gọi Art.create()
    public static Art create() {
        return new Art();
    }

    // Fluent Setters riêng của Art
    public Art setAuthor(String author) {
        this.author = author;
        return self();
    }

    public Art setyearCreated(String yearCreated) { 
        this.yearCreated = yearCreated; return self(); }

    // HÀM QUAN TRỌNG NHẤT: Kiểm tra toàn bộ Exception trước khi trả về
    public Art validate() throws ItemValidationException {
        if (getItemName() == null || getItemName().isEmpty()) 
            throw new ItemValidationException.MissingFieldException("Tên tác phẩm không được trống");
        
        if (getStartingPrice() <= 0)
            throw new ItemValidationException.InvalidPriceException("Giá khởi điểm phải > 0");

        if (author == null || author.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu tên tác giả");

        return this; // Trả về chính đối tượng đã "sạch" lỗi
    }
}