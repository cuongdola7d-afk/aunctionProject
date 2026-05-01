package ddc.server.pattern.Singleton;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ddc.server.exception.AuctionNotFoundException;
import ddc.server.exception.BidderNotFoundException;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;

public class AuctionManagerTest {

    @Test
    void getInstance_shouldAlwaysReturnSameObject() {
        AuctionManager manager1 = AuctionManager.getInstance();
        AuctionManager manager2 = AuctionManager.getInstance();

        assertSame(manager1, manager2);
    }

    @Test
    void addAuction_andGetAuction_shouldReturnStoredAuction() {
        AuctionManager manager = AuctionManager.getInstance();

        Auction auction = new Auction();
        auction.setId("AUCT-TEST-001");

        manager.addAuction(auction);

        Auction result = manager.getAuction("AUCT-TEST-001");

        assertNotNull(result);
        assertSame(auction, result);
    }

    @Test
    void addBidder_andGetBidder_shouldReturnStoredBidder() {
        AuctionManager manager = AuctionManager.getInstance();

        Bidder bidder = new Bidder();
        bidder.setId("BIDDER-TEST-001");

        manager.addBidder(bidder);

        Bidder result = manager.getBidder("BIDDER-TEST-001");

        assertNotNull(result);
        assertSame(bidder, result);
    }

    @Test
    void getAuctionOrThrow_shouldThrowWhenAuctionIdInvalid() {
        AuctionManager manager = AuctionManager.getInstance();

        assertThrows(AuctionNotFoundException.class, () -> manager.getAuctionOrThrow(""));
    }

    @Test
    void getAuctionOrThrow_shouldThrowWhenAuctionNotFound() {
        AuctionManager manager = AuctionManager.getInstance();

        assertThrows(AuctionNotFoundException.class, () -> manager.getAuctionOrThrow("AUCT-NOT-FOUND"));
    }

    @Test
    void getBidderOrThrow_shouldThrowWhenBidderIdInvalid() {
        AuctionManager manager = AuctionManager.getInstance();

        assertThrows(BidderNotFoundException.class, () -> manager.getBidderOrThrow(""));
    }

    @Test
    void getBidderOrThrow_shouldThrowWhenBidderNotFound() {
        AuctionManager manager = AuctionManager.getInstance();

        assertThrows(BidderNotFoundException.class, () -> manager.getBidderOrThrow("BIDDER-NOT-FOUND"));
    }

    @Test
    void getAuctionOrThrow_shouldNotThrowWhenAuctionExists() {
        AuctionManager manager = AuctionManager.getInstance();

        Auction auction = new Auction();
        auction.setId("AUCT-TEST-002");
        manager.addAuction(auction);

        assertDoesNotThrow(() -> manager.getAuctionOrThrow("AUCT-TEST-002"));
    }

    @Test
    void getBidderOrThrow_shouldNotThrowWhenBidderExists() {
        AuctionManager manager = AuctionManager.getInstance();

        Bidder bidder = new Bidder();
        bidder.setId("BIDDER-TEST-002");
        manager.addBidder(bidder);

        assertDoesNotThrow(() -> manager.getBidderOrThrow("BIDDER-TEST-002"));
    }
}