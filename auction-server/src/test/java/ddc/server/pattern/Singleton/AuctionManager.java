package ddc.server.pattern.Singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ddc.server.exception.AuctionNotFoundException;
import ddc.server.exception.BidderNotFoundException;

class AuctionManagerTest {

    private AuctionManager auctionManager;

    @BeforeEach
    void setUp() {
        auctionManager = AuctionManager.getInstance();
    }

    @Test
    void getAuctionOrThrow_shouldThrowWhenAuctionIdIsNull() {
        AuctionNotFoundException exception = assertThrows(
                AuctionNotFoundException.class,
                () -> auctionManager.getAuctionOrThrow(null)
        );

        assertEquals("auctionId không hợp lệ.", exception.getMessage());
    }

    @Test
    void getAuctionOrThrow_shouldThrowWhenAuctionIdIsBlank() {
        AuctionNotFoundException exception = assertThrows(
                AuctionNotFoundException.class,
                () -> auctionManager.getAuctionOrThrow("   ")
        );

        assertEquals("auctionId không hợp lệ.", exception.getMessage());
    }

    @Test
    void getAuctionOrThrow_shouldThrowWhenAuctionDoesNotExist() {
        String missingAuctionId = "AUCT-" + UUID.randomUUID();

        AuctionNotFoundException exception = assertThrows(
                AuctionNotFoundException.class,
                () -> auctionManager.getAuctionOrThrow(missingAuctionId)
        );

        assertEquals("Không tìm thấy phiên đấu giá: " + missingAuctionId, exception.getMessage());
    }

    @Test
    void getBidderOrThrow_shouldThrowWhenBidderIdIsNull() {
        BidderNotFoundException exception = assertThrows(
                BidderNotFoundException.class,
                () -> auctionManager.getBidderOrThrow(null)
        );

        assertEquals("bidderId không hợp lệ.", exception.getMessage());
    }

    @Test
    void getBidderOrThrow_shouldThrowWhenBidderIdIsBlank() {
        BidderNotFoundException exception = assertThrows(
                BidderNotFoundException.class,
                () -> auctionManager.getBidderOrThrow("   ")
        );

        assertEquals("bidderId không hợp lệ.", exception.getMessage());
    }

    @Test
    void getBidderOrThrow_shouldThrowWhenBidderDoesNotExist() {
        String missingBidderId = "BID-" + UUID.randomUUID();

        BidderNotFoundException exception = assertThrows(
                BidderNotFoundException.class,
                () -> auctionManager.getBidderOrThrow(missingBidderId)
        );

        assertEquals("Không tìm thấy bidder: " + missingBidderId, exception.getMessage());
    }
}