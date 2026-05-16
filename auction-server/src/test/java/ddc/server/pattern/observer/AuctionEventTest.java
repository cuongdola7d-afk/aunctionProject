package ddc.server.pattern.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ddc.server.model.item.General;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.User;

class AuctionEventTest {

    @Test
    void auctionStarted_shouldCopyAuctionAndItemFields() {
        Auction auction = sampleAuction();

        AuctionEvent event = AuctionEvent.auctionStarted(auction);

        assertEquals(AuctionEventType.AUCTION_STARTED, event.getType());
        assertEquals("A001", event.getAuctionId());
        assertEquals("I001", event.getItemId());
        assertEquals("Camera", event.getItemName());
        assertEquals(200, event.getCurrentPrice());
        assertEquals(AuctionStatus.OPEN, event.getStatus());
        assertEquals("Auction started", event.getMessage());
        assertNotNull(event.getEventTime());
    }

    @Test
    void auctionStarted_shouldBeNullSafe() {
        AuctionEvent event = AuctionEvent.auctionStarted(null);

        assertEquals(AuctionEventType.AUCTION_STARTED, event.getType());
        assertNull(event.getAuctionId());
        assertNull(event.getItemId());
        assertNull(event.getItemName());
        assertEquals(0, event.getCurrentPrice());
        assertNull(event.getStatus());
    }

    @Test
    void newBid_shouldCopyBidderAndBidAmount() {
        User bidder = new User().setName("Buyer One");
        Bid bid = new Bid().setBidder(bidder).setBidAmount(250);
        Auction auction = sampleAuction();

        AuctionEvent event = AuctionEvent.newBid(auction, bid);

        assertEquals(AuctionEventType.NEW_BID, event.getType());
        assertEquals("Buyer One", event.getBidderName());
        assertEquals(250, event.getBidAmount());
        assertEquals(200, event.getCurrentPrice());
        assertEquals("New bid placed", event.getMessage());
    }

    @Test
    void newBid_shouldAllowNullBid() {
        AuctionEvent event = AuctionEvent.newBid(sampleAuction(), null);

        assertEquals(AuctionEventType.NEW_BID, event.getType());
        assertNull(event.getBidderName());
        assertEquals(0, event.getBidAmount());
    }

    @Test
    void statusChanged_shouldUseNewStatusAndDescribeTransition() {
        AuctionEvent event = AuctionEvent.statusChanged(
                sampleAuction(),
                AuctionStatus.OPEN,
                AuctionStatus.RUNNING
        );

        assertEquals(AuctionEventType.STATUS_CHANGED, event.getType());
        assertEquals(AuctionStatus.RUNNING, event.getStatus());
        assertEquals("Status changed from OPEN to RUNNING", event.getMessage());
    }

    @Test
    void constructorAndSetters_shouldRoundTripValues() {
        LocalDateTime time = LocalDateTime.of(2026, 5, 16, 12, 0);
        AuctionEvent event = new AuctionEvent();

        event.setType(AuctionEventType.AUCTION_CANCELLED);
        event.setAuctionId("A002");
        event.setItemId("I002");
        event.setItemName("Watch");
        event.setBidderName("Buyer Two");
        event.setBidAmount(300);
        event.setCurrentPrice(275);
        event.setStatus(AuctionStatus.FINISHED);
        event.setEventTime(time);
        event.setMessage("Done");

        assertEquals(AuctionEventType.AUCTION_CANCELLED, event.getType());
        assertEquals("A002", event.getAuctionId());
        assertEquals("I002", event.getItemId());
        assertEquals("Watch", event.getItemName());
        assertEquals("Buyer Two", event.getBidderName());
        assertEquals(300, event.getBidAmount());
        assertEquals(275, event.getCurrentPrice());
        assertEquals(AuctionStatus.FINISHED, event.getStatus());
        assertEquals(time, event.getEventTime());
        assertEquals("Done", event.getMessage());
    }

    private Auction sampleAuction() {
        General item = new General()
                .setId("I001")
                .setItemName("Camera");

        return new Auction()
                .setId("A001")
                .setItem(item)
                .setCurrentPrice(200);
    }
}
