package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public String toString() {
        return super.toString() + String.format(" | Tac gia: %s | Nam sang tac: %s", author, yearCreated);
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
    
    // Trong file Art.java
    @Override
    public void loadSpecificDetails(Connection con) throws SQLException {
        String sql = "SELECT author, year_created FROM item_art WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, this.getId()); // Lấy ID của chính món đồ này
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    this.author = rs.getString("author");
                    this.yearCreated = rs.getString("year_created");
                }
            }
        }
    }
    
    // Kiểm tra toàn bộ Exception trước khi trả về
    public void validate() throws ItemValidationException {
        super.validate();

        if (author == null || author.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu tên tác giả");

        if (yearCreated == null || yearCreated.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu tên năm sáng tác");

    }
}