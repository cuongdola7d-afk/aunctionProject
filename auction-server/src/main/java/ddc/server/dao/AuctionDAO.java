package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.transaction.Auction;

public class AuctionDAO {
    private final ItemDAO itemDAO = new ItemDAO();
    private final UserDAO userDAO = new UserDAO();

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

    public List<Auction> getAllAuctions() {
        List<Auction> list = new ArrayList<>();
        String sql = "SELECT * FROM ddc_auctions";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Auction a = new Auction();
                a.setAuctionId(rs.getString("id"));
                a.setItem(itemDAO.getItem(rs.getString("item_id")));
                a.setStatus(rs.getString("status"));
                a.setHighestBidder(userDAO.getUser(rs.getString("highest_bidder_name")));
                a.setCurrentPrice(rs.getDouble("current_price"));
                a.setStartTime(rs.getObject("start_time", LocalDateTime.class));
                a.setEndTime(rs.getObject("end_time", LocalDateTime.class));
                list.add(a);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}