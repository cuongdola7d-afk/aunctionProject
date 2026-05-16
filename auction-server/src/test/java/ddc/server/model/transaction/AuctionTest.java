package ddc.server.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ddc.server.model.user.User;

class AuctionTest {

    @Test
    void newAuction_shouldStartOpenWithEmptyBidHistory() {
        Auction auction = new Auction();

        assertEquals(AuctionStatus.OPEN, auction.getStatus());
        assertTrue(auction.getBidHistory().isEmpty());
    }

    @Test
    void startAndEndAuction_shouldUpdateStatus() {
        Auction auction = new Auction();

        auction.startAuction();
        assertEquals(AuctionStatus.RUNNING, auction.getStatus());

        auction.endAuction();
        assertEquals(AuctionStatus.FINISHED, auction.getStatus());
    }

    @Test
    void placeBid_shouldRejectWhenAuctionNotRunning() {
        Auction auction = new Auction().setCurrentPrice(100);
        Bid bid = new Bid().setBidAmount(150);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> auction.placeBid(bid));

        assertEquals("Auction not running.", ex.getMessage());
    }

    @Test
    void placeBid_shouldRejectAmountNotHigherThanCurrentPrice() {
        Auction auction = new Auction().setCurrentPrice(100);
        auction.startAuction();

        Bid bid = new Bid().setBidAmount(100);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> auction.placeBid(bid));

        assertEquals("Bidded amount lower the current.", ex.getMessage());
    }

    @Test
    void placeBid_shouldStoreBidUpdatePriceAndHighestBidder() {
        User bidder = new User().setName("Buyer One");
        Bid bid = new Bid()
                .setAuctionId("A001")
                .setBidder(bidder)
                .setBidAmount(125)
                .setBidTime(LocalDateTime.now());
        Auction auction = new Auction()
                .setId("A001")
                .setCurrentPrice(100);
        auction.startAuction();

        auction.placeBid(bid);

        assertEquals(125, auction.getCurrentPrice());
        assertSame(bidder, auction.getHighestBidder());
        assertEquals(1, auction.getBidHistory().size());
        assertSame(bid, auction.getBidHistory().getFirst());
    }

    @Test
    void getMinBidIncrement_shouldCeilTenPercentOfStartingPrice() {
        Auction auction = new Auction().setStartingPrice(101);

        assertEquals(11, auction.getMinBidIncrement());
    }

    @Test
    void antiSnipConfig_shouldBeFluentAndOverrideDefaults() {
        Auction auction = new Auction();

        Auction result = auction
                .setAntiSnipThresholdSeconds(45)
                .setAntiSnipExtensionSeconds(20);

        assertSame(auction, result);
        assertEquals(45, auction.getAntiSnipThresholdSeconds());
        assertEquals(20, auction.getAntiSnipExtensionSeconds());
    }
}
