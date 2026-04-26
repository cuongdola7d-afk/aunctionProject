package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.transaction.Auction;

public class AuctionDAO {

    public boolean createAuction(Auction auction) {
        String sql = "INSERT INTO ddc_auctions (item_id, current_price, start_time, end_time) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, auction.getItem().getId());
            pst.setDouble(2, auction.getCurrentPrice());
            pst.setTimestamp(3, Timestamp.valueOf(auction.getStartTime()));
            pst.setTimestamp(4, Timestamp.valueOf(auction.getEndTime()));

            int insert = pst.executeUpdate();
            return insert > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}