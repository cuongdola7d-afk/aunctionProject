package ddc.server.controller.service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.InvalidBidException;
import ddc.server.model.item.General;
import ddc.server.model.item.ItemGeneric;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.Bidder;

class AuctionServiceTest {
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionService = new AuctionService();
    }

    @Test
    void refreshAuctionStatus_shouldSetOpenBeforeStartTime() {
        Auction auction = validAuction()
                .setStartTime(LocalDateTime.now().plusMinutes(10))
                .setEndTime(LocalDateTime.now().plusMinutes(20));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.OPEN, auction.getStatus());
    }

    @Test
    void refreshAuctionStatus_shouldSetRunningBetweenStartAndEndTime() {
        Auction auction = validAuction();

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.RUNNING, auction.getStatus());
    }

    @Test
    void refreshAuctionStatus_shouldSetFinishedAfterEndTime() {
        Auction auction = validAuction()
                .setStartTime(LocalDateTime.now().minusMinutes(20))
                .setEndTime(LocalDateTime.now().minusMinutes(10));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.FINISHED, auction.getStatus());
    }

    @Test
    void refreshAuctionStatus_shouldKeepCancelledStatus() {
        Auction auction = validAuction().setStatus("CANCELLED");

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.CANCELLED, auction.getStatus());
    }

    @Test
    void startAuction_shouldRejectAuctionBeforeStartTime() {
        Auction auction = validAuction()
                .setStartTime(LocalDateTime.now().plusMinutes(5))
                .setEndTime(LocalDateTime.now().plusMinutes(15));

        assertThrows(AuctionClosedException.class, () -> auctionService.startAuction(auction));
    }

    @Test
    void startAuction_shouldRejectFinishedAuction() {
        Auction auction = validAuction()
                .setStartTime(LocalDateTime.now().minusMinutes(20))
                .setEndTime(LocalDateTime.now().minusMinutes(10));

        assertThrows(AuctionClosedException.class, () -> auctionService.startAuction(auction));
    }

    @Test
    void startAuction_shouldRejectCancelledAuction() {
        Auction auction = validAuction().setStatus("CANCELLED");

        assertThrows(AuctionClosedException.class, () -> auctionService.startAuction(auction));
    }

    @Test
    void startAuction_shouldSetRunningWhenAuctionCanStart() throws Exception {
        Auction auction = validAuction();

        auctionService.startAuction(auction);

        assertEquals(AuctionStatus.RUNNING, auction.getStatus());
    }

    @Test
    void startAuction_shouldValidateRequiredStructure() {
        assertThrows(InvalidBidException.class, () -> auctionService.startAuction(null));
        assertThrows(InvalidBidException.class, () -> auctionService.startAuction(new Auction()));
        assertThrows(InvalidBidException.class, () -> auctionService.startAuction(
                new Auction().setItem(validItem())));
        assertThrows(InvalidBidException.class, () -> auctionService.startAuction(
                validAuction()
                        .setStartTime(LocalDateTime.now())
                        .setEndTime(LocalDateTime.now().minusSeconds(1))));
    }

    @Test
    void placeBid_shouldRejectNullBidder() {
        Auction auction = validAuction();

        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, null, 120, LocalDateTime.now()));
    }

    @Test
    void placeBid_shouldRejectBeforeAuctionStarts() {
        Auction auction = validAuction()
                .setStartTime(LocalDateTime.now().plusMinutes(5))
                .setEndTime(LocalDateTime.now().plusMinutes(15));

        assertThrows(AuctionClosedException.class,
                () -> auctionService.placeBid(auction, bidder("B001"), 120, LocalDateTime.now()));
    }

    @Test
    void placeBid_shouldRejectFinishedAuction() {
        Auction auction = validAuction()
                .setStartTime(LocalDateTime.now().minusMinutes(20))
                .setEndTime(LocalDateTime.now().minusMinutes(10));

        assertThrows(AuctionClosedException.class,
                () -> auctionService.placeBid(auction, bidder("B001"), 120, LocalDateTime.now()));
    }

    @Test
    void placeBid_shouldRejectDecimalAmount() {
        Auction auction = validAuction();

        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, bidder("B001"), 120.5, LocalDateTime.now()));
    }

    @Test
    void placeBid_shouldRejectAmountBelowMinimumIncrement() {
        Auction auction = validAuction();

        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, bidder("B001"), 105, LocalDateTime.now()));
    }

    @Test
    void placeBid_shouldRejectCancelledAuction() {
        Auction auction = validAuction().setStatus("CANCELLED");

        assertThrows(AuctionClosedException.class,
                () -> auctionService.placeBid(auction, bidder("B001"), 120, LocalDateTime.now()));
    }

    @Test
    void placeBid_shouldRejectSellerBiddingOnOwnItem() {
        Auction auction = validAuction();
        Bidder seller = bidder("B001");
        seller.setUsername("seller");

        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, seller, 120, LocalDateTime.now()));
    }

    @Test
    void placeBid_shouldRejectCurrentHighestBidderBiddingAgain() {
        Bidder bidder = bidder("B001");
        Auction auction = validAuction().setHighestBidder(bidder);

        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, bidder, 120, LocalDateTime.now()));
    }

    @Test
    void placeBid_shouldAcceptValidBidAndUpdateAuctionAndBidderHistory() throws Exception {
        Bidder bidder = bidder("B001");
        Auction auction = validAuction();
        LocalDateTime bidTime = LocalDateTime.now();

        boolean extended = auctionService.placeBid(auction, bidder, 120, bidTime);

        assertFalse(extended);
        assertEquals(120, auction.getCurrentPrice());
        assertSame(bidder, auction.getHighestBidder());
        assertEquals(1, auction.getBidHistory().size());
        assertEquals(1, bidder.getBidHistory().size());
        assertEquals("A001", auction.getBidHistory().getFirst().getAuction().getId());
    }

    @Test
    void placeBid_shouldExtendEndTimeWhenBidIsInsideAntiSnipWindow() throws Exception {
        Bidder bidder = bidder("B001");
        LocalDateTime start = LocalDateTime.now().minusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusSeconds(30);
        Auction auction = validAuction()
                .setStartTime(start)
                .setEndTime(end)
                .setAntiSnipThresholdSeconds(60)
                .setAntiSnipExtensionSeconds(30);

        boolean extended = auctionService.placeBid(auction, bidder, 120, LocalDateTime.now());

        assertTrue(extended);
        assertEquals(end.plusSeconds(30), auction.getEndTime());
    }

    @Test
    void cancelAuction_shouldSetCancelledForOpenAuction() throws Exception {
        Auction auction = validAuction();

        auctionService.cancelAuction(auction);

        assertEquals(AuctionStatus.CANCELLED, auction.getStatus());
    }

    @Test
    void cancelAuction_shouldRejectFinishedAuction() {
        Auction auction = validAuction().setStatus("FINISHED");

        assertThrows(AuctionClosedException.class, () -> auctionService.cancelAuction(auction));
    }

    @Test
    void cancelAuction_shouldReturnWhenAlreadyCancelled() throws Exception {
        Auction auction = validAuction().setStatus("CANCELLED");

        auctionService.cancelAuction(auction);

        assertEquals(AuctionStatus.CANCELLED, auction.getStatus());
    }

    @Test
    void finishAuction_shouldValidateAndCloseOpenAuction() throws Exception {
        Auction auction = validAuction();

        auctionService.finishAuction(auction);

        assertEquals(AuctionStatus.FINISHED, auction.getStatus());
    }

    @Test
    void finishAuction_shouldReturnForTerminalAuctions() throws Exception {
        Auction finished = validAuction().setStatus("FINISHED");
        Auction cancelled = validAuction().setStatus("CANCELLED");

        auctionService.finishAuction(finished);
        auctionService.finishAuction(cancelled);

        assertEquals(AuctionStatus.FINISHED, finished.getStatus());
        assertEquals(AuctionStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void finishAuction_shouldRejectInvalidStructure() {
        assertThrows(InvalidBidException.class, () -> auctionService.finishAuction(null));
        assertThrows(InvalidBidException.class, () -> auctionService.finishAuction(new Auction()));
    }

    @Test
    void getters_shouldBeNullSafeAndReturnAuctionState() {
        Auction auction = validAuction();
        Bidder highest = bidder("B009");
        Bid bid = new Bid().setBidder(highest).setBidAmount(120);
        auction.setHighestBidder(highest);
        auction.startAuction();
        auction.placeBid(bid);

        assertNull(auctionService.getHighestBidder(null));
        assertEquals(0, auctionService.getCurrentPrice(null));
        assertTrue(auctionService.getBidHistory(null).isEmpty());
        assertSame(highest, auctionService.getHighestBidder(auction));
        assertEquals(120, auctionService.getCurrentPrice(auction));
        assertIterableEquals(auction.getBidHistory(), auctionService.getBidHistory(auction));
    }

    @Test
    void applyAntiSnipExtension_shouldIgnoreNullOrOutsideWindow() {
        LocalDateTime end = LocalDateTime.now().plusMinutes(5);
        Auction auction = validAuction().setEndTime(end);

        assertFalse(auctionService.applyAntiSnipExtension(auction, null));
        assertEquals(end, auction.getEndTime());

        assertFalse(auctionService.applyAntiSnipExtension(
                auction,
                end.minusSeconds(auction.getAntiSnipThresholdSeconds() + 1L)));
        assertEquals(end, auction.getEndTime());

        assertFalse(auctionService.applyAntiSnipExtension(auction, end.plusSeconds(1)));
        assertEquals(end, auction.getEndTime());
    }

    private Auction validAuction() {
        return new Auction()
                .setId("A001")
                .setItem(validItem())
                .setCurrentPrice(100)
                .setStartingPrice(100)
                .setStartTime(LocalDateTime.now().minusMinutes(5))
                .setEndTime(LocalDateTime.now().plusMinutes(5));
    }

    private ItemGeneric<?> validItem() {
        return General.create()
                .setId("I001")
                .setItemName("Test item")
                .setCategory("GENERAL")
                .setSellerName("seller");
    }

    private Bidder bidder(String id) {
        Bidder bidder = new Bidder();
        bidder.setId(id);
        bidder.setUsername("bidder-" + id);
        return bidder;
    }
}
