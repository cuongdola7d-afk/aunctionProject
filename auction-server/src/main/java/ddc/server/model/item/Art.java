package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ddc.server.exception.ItemValidationException;

// Kế thừa ItemGeneric và truyền chính nó vào Generic T
public class Art extends ItemGeneric<Art> {
    private String author;
    private int yearCreated;

    public Art() {
        setCategory("ART");
    }

    // Static Factory Method: Để Creator gọi Art.create()
    public static Art create() {
        return new Art();
    }

    //Getters
    public String getAuthor() { return author; }
    public int getyearCreated() { return yearCreated; }

    //Setters
    public Art setAuthor (String author) {
        this.author = author;
        return this;
    }

    public Art setyearCreated (int yearCreated) {
        this.yearCreated = yearCreated;
        return this;
    }

    @Override
    public String save(Connection con) throws SQLException {
        String sqlInsert = "CALL insert_art (?, ?, ?, ?, ?, ?)";

        String sqlGetId = "SELECT @item_id AS generated_id;";
        
        try (PreparedStatement pst1 = con.prepareStatement(sqlInsert)) {
            pst1.setString(1, getItemName());
            pst1.setString(2, getCategory());
            pst1.setString(3, getDescription());
            pst1.setString(4, getSellerName());
            pst1.setString(5, author);
            pst1.setInt(6, yearCreated);
            
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
    
    // Trong file Art.java
    @Override
    public void loadSpecificDetails(Connection con) throws SQLException {
        String sql = "SELECT author, year_created FROM item_art WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, this.getId()); // Lấy ID của chính món đồ này
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    this.author = rs.getString("author");
                    this.yearCreated = rs.getInt("year_created");
                }
            }
        }
    }
    
    // Kiểm tra toàn bộ Exception trước khi trả về
    public void validate() throws ItemValidationException {
        // super.validate();

        if (author == null || author.isEmpty())
            throw new ItemValidationException.MissingFieldException("Thiếu tên tác giả");

        // if (yearCreated == null || yearCreated.isEmpty())
        //     throw new ItemValidationException.MissingFieldException("Thiếu tên năm sáng tác");

    }
}