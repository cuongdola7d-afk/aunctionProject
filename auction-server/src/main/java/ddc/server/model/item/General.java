package ddc.server.model.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class General extends ItemGeneric<General> {
    private static final Logger LOGGER = LoggerFactory.getLogger(General.class);
    public General () {}

    public static General create() {
        return new General();
    }

    @Override
    public String save (Connection con) {
        String sqlInsert = "INSERT INTO ddc_items (item_name, category, description, seller_name, image_url) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst1 = con.prepareStatement(sqlInsert)) {
            pst1.setString(1, getItemName());
            pst1.setString(2, getCategory());
            pst1.setString(3, getDescription());
            pst1.setString(4, getSellerName());
            pst1.setString(5, getImageUrl());

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
            LOGGER.error("Loi luu General item", e);
            return null;
        }
    }

    @Override
    public void load(Connection con) {} 

    @Override
    public void validate() {}
}