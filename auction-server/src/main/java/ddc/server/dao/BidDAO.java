package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.transaction.Bid;

public class BidDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(BidDAO.class);
    private final UserDAO userDAO = new UserDAO();
    private final AuctionDAO auctionDAO = new AuctionDAO();

    public boolean insertBid(Bid bid) {
        String sql = "INSERT INTO ddc_bids (bidder_name, bid_amount, bid_time, auction_id) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, bid.getBidder().getUsername());
            pst.setDouble(2, bid.getBidAmount());
            pst.setTimestamp(3, Timestamp.valueOf(bid.getBidTime()));
            pst.setString(4, bid.getAuction().getId());

            int insert = pst.executeUpdate();
            return insert > 0;

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Bid> getAllUserBid(String username) {
        List<Bid> list = new ArrayList<>();
        String sql = "SELECT b.id, b.bidder_name, b.bid_amount, b.bid_time, b.auction_id FROM ddc_bids b INNER JOIN ddc_users u ON b.bidder_name = u.username WHERE bidder_name = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username.trim());
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Bid bid = new Bid();
                    bid.setId(rs.getString("id"))
                    .setAuction(auctionDAO.getAuctionById(rs.getString("auction_id")))
                    .setBidAmount(rs.getDouble("bid_amount"))
                    .setBidder(userDAO.getUser(rs.getString("bidder_name")))
                    .setBidTime(rs.getObject("bid_time", LocalDateTime.class));
                    list.add(bid);
                }
            }  
        } catch (SQLException e) {
            LOGGER.error("Loi lay danh sach bid", e);
            e.printStackTrace();
        }

        return list;
    } 
}
