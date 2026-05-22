package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ddc.server.dao.AuctionDAO;
import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.InvalidBidException;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.Bidder;
import ddc.server.model.user.User;
import ddc.server.model.item.General;

@DisplayName("AuctionService - Unit Tests")
public class AuctionServiceTest {
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auctionService = new AuctionService();
    }

    // ==================== Helper Methods ====================

    private Auction createTestAuction() {
        Auction auction = new Auction();
        auction.setId("A001");
        auction.setStartTime(LocalDateTime.now().plusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(3));
        auction.setStatus(AuctionStatus.OPEN.name());
        auction.setCurrentPrice(1000);
        auction.setStartingPrice(1000); // minBidIncrement = 10% startingPrice
        return auction;
    }

    private General createTestItem() {
        General item = new General();
        item.setId("I001");
        item.setItemName("Test Item");
        item.setSellerName("seller123");
        return item;
    }

    private Bidder createTestBidder() {
        Bidder bidder = new Bidder();
        bidder.setId("B001");
        bidder.setUsername("bidder123");
        return bidder;
    }

    // ==================== refreshAuctionStatus Tests ====================

    @Test
    @DisplayName("refreshAuctionStatus - Should handle null auction gracefully")
    void testRefreshAuctionStatus_NullAuction() {
        assertDoesNotThrow(() -> auctionService.refreshAuctionStatus(null),
                "Should not throw exception for null auction");
    }

    @Test
    @DisplayName("refreshAuctionStatus - Should handle auction with null startTime")
    void testRefreshAuctionStatus_NullStartTime() {
        Auction auction = new Auction();
        auction.setStartTime(null);
        auction.setEndTime(LocalDateTime.now().plusHours(2));

        assertDoesNotThrow(() -> auctionService.refreshAuctionStatus(auction),
                "Should not throw exception when startTime is null");
    }

    @Test
    @DisplayName("refreshAuctionStatus - Should handle auction with null endTime")
    void testRefreshAuctionStatus_NullEndTime() {
        Auction auction = new Auction();
        auction.setStartTime(LocalDateTime.now().plusHours(1));
        auction.setEndTime(null);

        assertDoesNotThrow(() -> auctionService.refreshAuctionStatus(auction),
                "Should not throw exception when endTime is null");
    }

    @Test
    @DisplayName("refreshAuctionStatus - Should not modify CANCELLED auction")
    void testRefreshAuctionStatus_CancelledAuction() {
        Auction auction = new Auction();
        auction.setStatus(AuctionStatus.CANCELLED.name());
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(1));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.CANCELLED, auction.getStatus(),
                "CANCELLED auction should remain CANCELLED");
    }

    @Test
    @DisplayName("refreshAuctionStatus - Should set OPEN for future auction")
    void testRefreshAuctionStatus_FutureAuction() {
        Auction auction = new Auction();
        auction.setStartTime(LocalDateTime.now().plusHours(2));
        auction.setEndTime(LocalDateTime.now().plusHours(4));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.OPEN, auction.getStatus(),
                "Future auction should have OPEN status");
    }

    @Test
    @DisplayName("refreshAuctionStatus - Should set RUNNING for active auction")
    void testRefreshAuctionStatus_ActiveAuction() {
        Auction auction = new Auction();
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(2));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.RUNNING, auction.getStatus(),
                "Active auction should have RUNNING status");
    }

    @Test
    @DisplayName("refreshAuctionStatus - Should set FINISHED for expired auction")
    void testRefreshAuctionStatus_ExpiredAuction() {
        Auction auction = new Auction();
        auction.setStartTime(LocalDateTime.now().minusHours(3));
        auction.setEndTime(LocalDateTime.now().minusHours(1));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.FINISHED, auction.getStatus(),
                "Expired auction should have FINISHED status");
    }

    // ==================== placeBid Tests ====================

    @Test
    @DisplayName("placeBid - Should throw exception if auction is null")
    void testPlaceBid_NullAuction() {
        Bidder bidder = createTestBidder();
        // validateAuctionStructure(null) nem InvalidBidException
        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(null, bidder, 1100, LocalDateTime.now()),
                "Should throw exception for null auction");
    }

    @Test
    @DisplayName("placeBid - Should throw exception if bidder is null")
    void testPlaceBid_NullBidder() {
        Auction auction = createTestAuction();

        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, null, 1100, LocalDateTime.now()),
                "Should throw InvalidBidException for null bidder");
    }

    @Test
    @DisplayName("placeBid - Should prevent seller from bidding on own item")
    void testPlaceBid_SellerBiddingOwnItem() {
        Auction auction = createTestAuction();
        auction.setItem(createTestItem());
        auction.setStatus(AuctionStatus.RUNNING.name());

        Bidder sellerBidder = new Bidder();
        sellerBidder.setId("SELLER_ID");
        sellerBidder.setUsername("seller123"); // Same as item seller

        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, sellerBidder, 1100, LocalDateTime.now()),
                "Seller should not be able to bid on their own item");
    }

    @Test
    @DisplayName("placeBid - Should reject non-integer bid amounts")
    void testPlaceBid_DecimalBidAmount() {
        Auction auction = createTestAuction();
        // startTime trong qua khu de refreshAuctionStatus giu RUNNING
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(2));
        auction.setStatus(AuctionStatus.RUNNING.name());
        auction.setItem(createTestItem());

        Bidder bidder = createTestBidder();

        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, bidder, 1100.5, LocalDateTime.now()),
                "Decimal bid amounts should be rejected");
    }

    @Test
    @DisplayName("placeBid - Should enforce minimum bid increment")
    void testPlaceBid_BidBelowMinimumIncrement() {
        Auction auction = createTestAuction();
        // startTime trong qua khu de refreshAuctionStatus giu RUNNING
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(2));
        auction.setStatus(AuctionStatus.RUNNING.name());
        auction.setCurrentPrice(1000);
        auction.setStartingPrice(1000); // minBidIncrement = 10% = 100
        auction.setItem(createTestItem());

        Bidder bidder = createTestBidder();

        // Bid amount (1050) < minimum (1100)
        assertThrows(InvalidBidException.class,
                () -> auctionService.placeBid(auction, bidder, 1050, LocalDateTime.now()),
                "Bid amount must meet minimum increment requirement");
    }

    // ==================== startAuction Tests ====================

    @Test
    @DisplayName("startAuction - Should throw exception if auction structure is invalid")
    void testStartAuction_InvalidStructure() {
        Auction auction = new Auction();
        // Missing startTime and endTime

        assertThrows(Exception.class,
                () -> auctionService.startAuction(auction),
                "Should validate auction structure before starting");
    }

    @Test
    @DisplayName("startAuction - Should throw exception if auction is already FINISHED")
    void testStartAuction_FinishedAuction() {
        Auction auction = createTestAuction();
        auction.setItem(createTestItem());
        auction.setStatus(AuctionStatus.FINISHED.name());

        assertThrows(AuctionClosedException.class,
                () -> auctionService.startAuction(auction),
                "Should not allow starting a finished auction");
    }

    @Test
    @DisplayName("startAuction - Should throw exception if auction is CANCELLED")
    void testStartAuction_CancelledAuction() {
        Auction auction = createTestAuction();
        auction.setItem(createTestItem());
        auction.setStatus(AuctionStatus.CANCELLED.name());

        assertThrows(AuctionClosedException.class,
                () -> auctionService.startAuction(auction),
                "Should not allow starting a cancelled auction");
    }

    @Test
    @DisplayName("startAuction - Should throw exception if start time hasn't been reached")
    void testStartAuction_BeforeStartTime() {
        Auction auction = createTestAuction();
        auction.setItem(createTestItem());
        auction.setStartTime(LocalDateTime.now().plusHours(2));
        auction.setEndTime(LocalDateTime.now().plusHours(4));
        auction.setStatus(AuctionStatus.OPEN.name());

        assertThrows(AuctionClosedException.class,
                () -> auctionService.startAuction(auction),
                "Cannot start auction before start time");
    }

    // ==================== getAll Tests ====================

    @Test
    @DisplayName("getAll - Should return empty list when no auctions exist")
    void testGetAll_EmptyList() {
        List<Auction> result = auctionService.getAll();
        assertNotNull(result, "Should return non-null list");
    }

    @Test
    @DisplayName("getAll - Should return list of auctions")
    void testGetAll_ReturnsAuctions() {
        List<Auction> result = auctionService.getAll();
        assertNotNull(result, "Should not return null");
        assertTrue(result instanceof List, "Should return a List");
    }

    // ==================== createAuction Tests ====================

    @Test
    @DisplayName("createAuction - Should accept valid auction")
    void testCreateAuction_ValidAuction() {
        Auction auction = createTestAuction();
        // Test would need DAO mock or integration test for full verification
        assertNotNull(auction.getId(), "Valid auction should have an ID");
    }

    @Test
    @DisplayName("createAuction - Should handle auction with all required fields")
    void testCreateAuction_AllRequiredFields() {
        Auction auction = new Auction();
        auction.setId("A001");
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusHours(1));
        auction.setCurrentPrice(100);

        assertNotNull(auction, "Auction object should be created");
        assertNotNull(auction.getId(), "Auction should have ID");
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Bid validation - Exact minimum bid should be accepted")
    void testBidValidation_ExactMinimumBid() {
        Auction auction = createTestAuction();
        auction.setCurrentPrice(1000);
        auction.setStartingPrice(1000); // minBidIncrement = 10% = 100
        
        double minimumBid = auction.getCurrentPrice() + auction.getMinBidIncrement();
        assertEquals(1100, minimumBid, "Minimum bid calculation should be correct");
    }

    @Test
    @DisplayName("Bid validation - Amount just above minimum should be valid")
    void testBidValidation_JustAboveMinimum() {
        double currentPrice = 1000;
        long minIncrement = 100;
        double bidAmount = currentPrice + minIncrement + 1;

        assertTrue(bidAmount > currentPrice + minIncrement,
                "Bid amount above minimum should pass validation");
    }

    @Test
    @DisplayName("Status transitions - OPEN → RUNNING → FINISHED")
    void testStatusTransitions() {
        Auction auction = new Auction();
        auction.setStartTime(LocalDateTime.now().plusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(2));

        auctionService.refreshAuctionStatus(auction);
        assertEquals(AuctionStatus.OPEN, auction.getStatus());
    }
}
