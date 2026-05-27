package ddc.server.dao;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.User;

@DisplayName("BidDAO - Unit Tests")
class BidDAOTest {
    private BidDAO bidDAO;

    @BeforeEach
    void setUp() {
        bidDAO = new BidDAO();
    }

    // Kiểm tra insertBid với dữ liệu null không crash
    @Test
    @DisplayName("insertBid - trả về false khi không có kết nối DB")
    void insertBid_shouldReturnFalseWithoutDb() {
        User bidder = new User().setUsername("testbidder");
        Auction auction = new Auction().setId("A001");
        Bid bid = new Bid()
                .setBidder(bidder)
                .setBidAmount(500.0)
                .setBidTime(LocalDateTime.now())
                .setAuction(auction);

        // Không có DB config → insertBid trả về false
        boolean result = bidDAO.insertBid(bid);
        assertFalse(result);
    }

    // Kiểm tra getAllUserBid trả về empty list khi không có DB
    @Test
    @DisplayName("getAllUserBid - trả về danh sách rỗng khi không có DB")
    void getAllUserBid_shouldReturnEmptyListWithoutDb() {
        var result = bidDAO.getAllUserBid("nonexistent");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Kiểm tra getAllUserBid với username có khoảng trắng
    @Test
    @DisplayName("getAllUserBid - xử lý username có khoảng trắng")
    void getAllUserBid_shouldTrimUsername() {
        // Không crash khi username có khoảng trắng
        var result = bidDAO.getAllUserBid("  testuser  ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Kiểm tra Bid model getters/setters
    @Test
    @DisplayName("Bid model - getters/setters hoạt động đúng")
    void bidModel_gettersSetters_shouldWork() {
        User bidder = new User().setUsername("user01").setName("User 01");
        Auction auction = new Auction().setId("A001");
        LocalDateTime now = LocalDateTime.now();

        Bid bid = new Bid()
                .setId("B001")
                .setBidder(bidder)
                .setAuction(auction)
                .setBidAmount(1000.0)
                .setBidTime(now);

        assertEquals("B001", bid.getId());
        assertEquals("user01", bid.getBidder().getUsername());
        assertEquals("A001", bid.getAuction().getId());
        assertEquals(1000.0, bid.getBidAmount());
        assertEquals(now, bid.getBidTime());
    }

    // Kiểm tra Bid fluent setter chain
    @Test
    @DisplayName("Bid model - fluent setters trả về chính nó")
    void bidModel_fluentSetters_shouldReturnSelf() {
        Bid bid = new Bid();
        Bid result = bid.setBidAmount(100.0);
        assertEquals(bid, result);
    }
}
