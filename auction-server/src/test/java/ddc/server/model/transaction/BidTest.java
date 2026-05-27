package ddc.server.model.transaction;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.model.user.User;

@DisplayName("Bid Model - Unit Tests")
class BidTest {

    // Kiểm tra constructor mặc định
    @Test
    @DisplayName("constructor mặc định - tất cả fields null/0")
    void defaultConstructor_shouldHaveNullFields() {
        Bid bid = new Bid();

        assertNull(bid.getId());
        assertNull(bid.getAuction());
        assertNull(bid.getBidder());
        assertEquals(0.0, bid.getBidAmount());
        assertNull(bid.getBidTime());
    }

    // Kiểm tra fluent setter chain
    @Test
    @DisplayName("fluent setters - trả về chính đối tượng Bid")
    void fluentSetters_shouldReturnSelf() {
        Bid bid = new Bid();

        Bid result = bid.setId("B001")
                .setBidAmount(500.0)
                .setBidTime(LocalDateTime.now())
                .setBidder(new User())
                .setAuction(new Auction());

        assertEquals(bid, result);
    }

    // Kiểm tra getters trả về giá trị đúng
    @Test
    @DisplayName("getters - trả về giá trị đã set")
    void getters_shouldReturnSetValues() {
        User bidder = new User().setUsername("user1");
        Auction auction = new Auction().setId("A001");
        LocalDateTime time = LocalDateTime.of(2026, 5, 27, 12, 0);

        Bid bid = new Bid()
                .setId("B001")
                .setBidder(bidder)
                .setAuction(auction)
                .setBidAmount(1000.0)
                .setBidTime(time);

        assertEquals("B001", bid.getId());
        assertEquals("user1", bid.getBidder().getUsername());
        assertEquals("A001", bid.getAuction().getId());
        assertEquals(1000.0, bid.getBidAmount());
        assertEquals(time, bid.getBidTime());
    }

    // Kiểm tra bidAmount = 0
    @Test
    @DisplayName("bidAmount = 0 - giá trị hợp lệ")
    void bidAmount_zero_shouldBeAllowed() {
        Bid bid = new Bid().setBidAmount(0);
        assertEquals(0.0, bid.getBidAmount());
    }

    // Kiểm tra bidAmount số âm (model không validate)
    @Test
    @DisplayName("bidAmount âm - model cho phép (validation ở service)")
    void bidAmount_negative_shouldBeAllowedInModel() {
        Bid bid = new Bid().setBidAmount(-100);
        assertEquals(-100.0, bid.getBidAmount());
    }

    // Kiểm tra bid với số tiền rất lớn
    @Test
    @DisplayName("bidAmount lớn - không bị overflow")
    void bidAmount_large_shouldNotOverflow() {
        double largeAmount = 999_999_999_999.0;
        Bid bid = new Bid().setBidAmount(largeAmount);
        assertEquals(largeAmount, bid.getBidAmount());
    }

    // Kiểm tra ghi đè giá trị
    @Test
    @DisplayName("setter gọi lại - ghi đè giá trị cũ")
    void setter_calledTwice_shouldOverrideValue() {
        Bid bid = new Bid().setBidAmount(100.0);
        assertEquals(100.0, bid.getBidAmount());

        bid.setBidAmount(200.0);
        assertEquals(200.0, bid.getBidAmount());
    }

    // Kiểm tra kế thừa Entity - setId
    @Test
    @DisplayName("setId - kế thừa từ Entity hoạt động đúng")
    void setId_fromEntity_shouldWork() {
        Bid bid = new Bid().setId("BID-123");
        assertNotNull(bid.getId());
        assertEquals("BID-123", bid.getId());
    }
}
