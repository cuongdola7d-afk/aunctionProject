package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    //Getters
    public String getAuthor() { return author; }
    public String getyearCreated() { return yearCreated; }

    //Setters
    public Art setAuthor (String author) {
        this.author = author;
        return this;
    }

    public Art setyearCreated (String yearCreated) {
        this.yearCreated = yearCreated;
        return this;
    }

    @Override
    protected void saveSpecificDetails(Connection con, String itemId) throws SQLException {
        String sql = "INSERT INTO item_art (id, author, year_created) VALUES (?, ?, ?)";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, itemId);          // Dùng itemId nhận được từ cha
            pst.setString(2, this.author);       
            pst.setString(3, this.yearCreated); 
            
            pst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    // HÀM QUAN TRỌNG NHẤT: Kiểm tra toàn bộ Exception trước khi trả về
    public Art validate() throws ItemValidationException {
        if (getItemName() == null || getItemName().isEmpty()) 
            throw new ItemValidationException.MissingFieldException("Tên tác phẩm không được trống");
        
        // if (getStartingPrice() <= 0)
        //     throw new ItemValidationException.InvalidPriceException("Giá khởi điểm phải > 0");

        if (author == null || author.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu tên tác giả");

        return this; // Trả về chính đối tượng đã "sạch" lỗi
    }
}