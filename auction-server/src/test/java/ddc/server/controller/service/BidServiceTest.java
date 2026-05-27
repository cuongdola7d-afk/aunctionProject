package ddc.server.controller.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import ddc.server.dao.BidDAO;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.User;

@DisplayName("BidService - Unit Tests")
class BidServiceTest {
    @Mock
    private BidDAO bidDAO;

    private BidService bidService;

    // Inject mock BidDAO vào BidService bằng reflection (vì constructor dùng new)
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        bidService = new BidService();
        Field daoField = BidService.class.getDeclaredField("bidDAO");
        daoField.setAccessible(true);
        daoField.set(bidService, bidDAO);
    }

    // Kiểm tra getAll trả về danh sách bid hợp lệ
    @Test
    @DisplayName("getAll - trả về danh sách bid của user")
    void getAll_shouldReturnBidsForUser() {
        Bid bid1 = createBid("B001", 500.0);
        Bid bid2 = createBid("B002", 1000.0);
        when(bidDAO.getAllUserBid("testuser")).thenReturn(List.of(bid1, bid2));

        List<Bid> result = bidService.getAll("testuser");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(500.0, result.get(0).getBidAmount());
        assertEquals(1000.0, result.get(1).getBidAmount());
        verify(bidDAO).getAllUserBid("testuser");
    }

    // Kiểm tra getAll trả về danh sách rỗng khi user không có bid
    @Test
    @DisplayName("getAll - trả về danh sách rỗng khi user không có bid")
    void getAll_shouldReturnEmptyListWhenNoBids() {
        when(bidDAO.getAllUserBid("emptyuser")).thenReturn(List.of());

        List<Bid> result = bidService.getAll("emptyuser");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bidDAO).getAllUserBid("emptyuser");
    }

    // Kiểm tra getAll với username hợp lệ gọi đúng DAO
    @Test
    @DisplayName("getAll - verify gọi đúng DAO method với đúng username")
    void getAll_shouldDelegateToDAO() {
        when(bidDAO.getAllUserBid("user123")).thenReturn(List.of());

        bidService.getAll("user123");

        verify(bidDAO).getAllUserBid("user123");
    }

    // Kiểm tra getAll khi DAO trả về nhiều bid với dữ liệu đầy đủ
    @Test
    @DisplayName("getAll - kiểm tra dữ liệu bid trả về đầy đủ thông tin")
    void getAll_shouldReturnBidsWithCompleteData() {
        User bidder = new User().setUsername("bidder01").setName("Bidder One");
        Auction auction = new Auction().setId("A001").setCurrentPrice(1000);
        Bid bid = new Bid()
                .setId("B001")
                .setBidder(bidder)
                .setAuction(auction)
                .setBidAmount(1500.0)
                .setBidTime(LocalDateTime.of(2026, 5, 27, 10, 0));

        when(bidDAO.getAllUserBid("bidder01")).thenReturn(List.of(bid));

        List<Bid> result = bidService.getAll("bidder01");

        assertEquals(1, result.size());
        Bid resultBid = result.getFirst();
        assertEquals("B001", resultBid.getId());
        assertEquals("bidder01", resultBid.getBidder().getUsername());
        assertEquals("A001", resultBid.getAuction().getId());
        assertEquals(1500.0, resultBid.getBidAmount());
    }

    // Helper: tạo Bid test
    private Bid createBid(String id, double amount) {
        return new Bid()
                .setId(id)
                .setBidAmount(amount)
                .setBidTime(LocalDateTime.now());
    }
}
