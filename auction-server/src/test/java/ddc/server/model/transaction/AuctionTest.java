package ddc.server.model.transaction;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.InvalidBidException;
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

        AuctionClosedException ex = assertThrows(AuctionClosedException.class, () -> auction.placeBid(bid));

        assertEquals("Auction not running.", ex.getMessage());
    }

    @Test
    void placeBid_shouldRejectAmountNotHigherThanCurrentPrice() {
        Auction auction = new Auction().setCurrentPrice(100);
        auction.startAuction();

        Bid bid = new Bid().setBidAmount(100);

        InvalidBidException ex = assertThrows(InvalidBidException.class, () -> auction.placeBid(bid));

        assertEquals("Bidded amount lower the current.", ex.getMessage());
    }

    @Test
    void placeBid_shouldStoreBidUpdatePriceAndHighestBidder() throws Exception {
        User bidder = new User().setName("Buyer One");
        Auction auction = new Auction()
                .setId("A001")
                .setCurrentPrice(100);
        Bid bid = new Bid()
                .setAuction(auction)
                .setBidder(bidder)
                .setBidAmount(125)
                .setBidTime(LocalDateTime.now());
        
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
