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
import ddc.server.model.transaction.Auction;

public class AuctionDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionDAO.class);
    private final ItemDAO itemDAO = new ItemDAO();
    private final UserDAO userDAO = new UserDAO();

    public boolean createAuction(Auction auction) {
        String sql = "INSERT INTO ddc_auctions (item_id, highest_bidder_name, current_price, starting_price, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, auction.getItem().getId());
            pst.setString(2, "username");
            pst.setDouble(3, auction.getCurrentPrice());
            pst.setDouble(4, auction.getStartingPrice() > 0 ? auction.getStartingPrice() : auction.getCurrentPrice());
            pst.setTimestamp(5, Timestamp.valueOf(auction.getStartTime()));
            pst.setTimestamp(6, Timestamp.valueOf(auction.getEndTime()));

            int insert = pst.executeUpdate();
            return insert > 0;
        } catch (Exception e) {
            LOGGER.error("Loi tao auction", e);
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
                a.setId(rs.getString("id"));
                a.setItem(itemDAO.getItem(rs.getString("item_id")));
                a.setStatus(rs.getString("status"));
                a.setHighestBidder(userDAO.getUser(rs.getString("highest_bidder_name")));
                a.setCurrentPrice(rs.getDouble("current_price"));
                a.setStartingPrice(rs.getDouble("starting_price"));
                a.setStartTime(rs.getObject("start_time", LocalDateTime.class));
                a.setEndTime(rs.getObject("end_time", LocalDateTime.class));
                list.add(a);
            }

        } catch (SQLException e) {
            LOGGER.error("Loi lay danh sach auction", e);
        }
        return list;
    }

    public List<Auction> getAllUserAuctions(String username) {
        List<Auction> list = new ArrayList<>();
        String sql = "SELECT a.id, a.item_id, a.status, a.highest_bidder_name, a.current_price, a.starting_price, start_time, end_time FROM ddc_auctions a INNER JOIN ddc_items i ON a.item_id = i.id WHERE seller_name = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);) {

            pst.setString(1, username.trim());
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Auction a = new Auction();
                    a.setId(rs.getString("id"));
                    a.setItem(itemDAO.getItem(rs.getString("item_id")));
                    a.setStatus(rs.getString("status"));
                    a.setHighestBidder(userDAO.getUser(rs.getString("highest_bidder_name")));
                    a.setCurrentPrice(rs.getDouble("current_price"));
                    a.setStartingPrice(rs.getDouble("starting_price"));
                    a.setStartTime(rs.getObject("start_time", LocalDateTime.class));
                    a.setEndTime(rs.getObject("end_time", LocalDateTime.class));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Loi lay danh sach auction", e);
        }
        return list;
    }

    // Lấy auction theo id từ DB
    public Auction getAuctionById(String auctionId) {
        String sql = "SELECT * FROM ddc_auctions WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, auctionId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Auction auction = new Auction();
                    auction.setId(rs.getString("id"));
                    auction.setItem(itemDAO.getItem(rs.getString("item_id")));
                    auction.setStatus(rs.getString("status"));
                    auction.setHighestBidder(userDAO.getUser(rs.getString("highest_bidder_name")));
                    auction.setCurrentPrice(rs.getDouble("current_price"));
                    auction.setStartingPrice(rs.getDouble("starting_price"));
                    auction.setStartTime(rs.getObject("start_time", LocalDateTime.class));
                    auction.setEndTime(rs.getObject("end_time", LocalDateTime.class));
                    return auction;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Loi lay auction theo id", e);
        }

        return null;
    }

    // Cập nhật giá, bidder, status của auction về DB
    public boolean updateAuction(Auction auction) {
        String sql = "UPDATE ddc_auctions SET current_price = ?, highest_bidder_name = ?, status = ?, end_time = ? WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setDouble(1, auction.getCurrentPrice());
            String bidderName = auction.getHighestBidder() != null
                    ? auction.getHighestBidder().getUsername()
                    : null;
            pst.setString(2, bidderName);
            pst.setString(3, auction.getStatus().name());
            pst.setTimestamp(4, auction.getEndTime() != null ? Timestamp.valueOf(auction.getEndTime()) : null);
            pst.setString(5, auction.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Loi cap nhat auction", e);
        }

        return false;
    }

}