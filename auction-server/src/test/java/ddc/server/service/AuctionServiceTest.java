package ddc.server.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.InvalidBidException;
import ddc.server.model.item.Item;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.BidTransaction;
import ddc.server.model.user.Bidder;

class AuctionServiceTest {

    private static final double DELTA = 0.0001;

    private AuctionService auctionService;
    private Bidder bidder;
    private TestItem item;

    @BeforeEach
    void setUp() {
        auctionService = new AuctionService();

        bidder = new Bidder();
        bidder.setName("duki123");
        bidder.setEmail("duki@example.com");
        bidder.setPassword("123456");

        item = new TestItem("Laptop Gaming", "Laptop demo", 10_000_000d);
    }

    @Test
    void createAuction_shouldCreateAuctionWithOpenStatus() throws InvalidBidException {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(5);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        Auction auction = auctionService.createAuction(item, startTime, endTime);

        assertAll(
                () -> assertNotNull(auction),
                () -> assertSame(item, auction.getItem()),
                () -> assertEquals(AuctionStatus.OPEN, auction.getStatus()),
                () -> assertEquals(item.getStartingPrice(), auction.getCurrentPrice(), DELTA),
                () -> assertEquals(item.getStartingPrice(), item.getCurrentPrice(), DELTA)
        );
    }

    @Test
    void createAuction_shouldCopyStartAndEndTimeCorrectly() throws InvalidBidException {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(10);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);

        Auction auction = auctionService.createAuction(item, startTime, endTime);

        assertAll(
                () -> assertEquals(startTime, auction.getStartTime()),
                () -> assertEquals(endTime, auction.getEndTime())
        );
    }

    @Test
    void startAuction_shouldSetStatusRunningWhenStartTimeReached()
            throws InvalidBidException, AuctionClosedException {

        Auction auction = createRunningAuction();

        auctionService.startAuction(auction);

        assertEquals(AuctionStatus.RUNNING, auction.getStatus());
        assertTrue(auction.isRunning());
    }

    @Test
    void placeBid_shouldUpdateCurrentPriceWhenBidIsValid()
            throws InvalidBidException, AuctionClosedException {

        Auction auction = createRunningAuction();
        double newBidAmount = 12_500_000d;

        auctionService.placeBid(auction, bidder, newBidAmount);

        assertEquals(newBidAmount, auctionService.getCurrentPrice(auction), DELTA);
    }

    @Test
    void placeBid_shouldUpdateHighestBidderWhenBidIsValid()
            throws InvalidBidException, AuctionClosedException {

        Auction auction = createRunningAuction();
        double newBidAmount = 12_500_000d;

        auctionService.placeBid(auction, bidder, newBidAmount);

        assertSame(bidder, auctionService.getHighestBidder(auction));
    }

    @Test
    void placeBid_shouldAddBidToAuctionBidHistory()
            throws InvalidBidException, AuctionClosedException {

        Auction auction = createRunningAuction();
        double newBidAmount = 12_500_000d;

        auctionService.placeBid(auction, bidder, newBidAmount);

        List<BidTransaction> bidHistory = auctionService.getBidHistory(auction);

        assertAll(
                () -> assertEquals(1, bidHistory.size()),
                () -> assertEquals(newBidAmount, bidHistory.get(0).getAmount(), DELTA),
                () -> assertSame(bidder, bidHistory.get(0).getBidder()),
                () -> assertEquals(auction.getId(), bidHistory.get(0).getAuctionId())
        );
    }

    @Test
    void placeBid_shouldAddBidToBidderHistory()
            throws InvalidBidException, AuctionClosedException {

        Auction auction = createRunningAuction();
        double newBidAmount = 12_500_000d;

        auctionService.placeBid(auction, bidder, newBidAmount);

        assertAll(
                () -> assertEquals(1, bidder.getBidHistory().size()),
                () -> assertEquals(newBidAmount, bidder.getBidHistory().get(0).getAmount(), DELTA),
                () -> assertEquals(auction.getId(), bidder.getBidHistory().get(0).getAuctionId())
        );
    }

    @Test
    void placeBid_shouldSyncItemCurrentPrice()
            throws InvalidBidException, AuctionClosedException {

        Auction auction = createRunningAuction();
        double newBidAmount = 12_500_000d;

        auctionService.placeBid(auction, bidder, newBidAmount);

        assertAll(
                () -> assertEquals(newBidAmount, auction.getCurrentPrice(), DELTA),
                () -> assertEquals(newBidAmount, item.getCurrentPrice(), DELTA)
        );
    }

    @Test
    void finishAuction_shouldSetStatusFinished() throws InvalidBidException {
        Auction auction = createRunningAuctionDirectly();

        auctionService.finishAuction(auction);

        assertEquals(AuctionStatus.FINISHED, auction.getStatus());
    }

    @Test
void createAuction_shouldThrowWhenItemIsNull() {
    LocalDateTime startTime = LocalDateTime.now().plusMinutes(5);
    LocalDateTime endTime = LocalDateTime.now().plusHours(1);

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.createAuction(null, startTime, endTime)
    );
}

@Test
void createAuction_shouldThrowWhenStartTimeIsNull() {
    LocalDateTime endTime = LocalDateTime.now().plusHours(1);

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.createAuction(item, null, endTime)
    );
}

@Test
void createAuction_shouldThrowWhenEndTimeIsNull() {
    LocalDateTime startTime = LocalDateTime.now().plusMinutes(5);

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.createAuction(item, startTime, null)
    );
}

@Test
void createAuction_shouldThrowWhenEndTimeIsNotAfterStartTime() {
    LocalDateTime startTime = LocalDateTime.now().plusHours(1);
    LocalDateTime endTime = LocalDateTime.now().plusMinutes(5);

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.createAuction(item, startTime, endTime)
    );
}

@Test
void startAuction_shouldThrowWhenAuctionIsNull() {
    assertThrows(
            InvalidBidException.class,
            () -> auctionService.startAuction(null)
    );
}

@Test
void startAuction_shouldThrowWhenAuctionItemIsNull() {
    Auction auction = new Auction();
    auction.setStartTime(LocalDateTime.now().minusMinutes(1));
    auction.setEndTime(LocalDateTime.now().plusMinutes(10));

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.startAuction(auction)
    );
}

@Test
void startAuction_shouldThrowWhenAuctionTimesAreInvalid() {
    Auction auction = new Auction(item,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusMinutes(5));

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.startAuction(auction)
    );
}

@Test
void startAuction_shouldThrowWhenAuctionCancelled() throws InvalidBidException {
    Auction auction = auctionService.createAuction(
            item,
            LocalDateTime.now().minusMinutes(10),
            LocalDateTime.now().plusMinutes(30)
    );
    auction.setStatus(AuctionStatus.CANCELLED);

    assertThrows(
            AuctionClosedException.class,
            () -> auctionService.startAuction(auction)
    );
}

@Test
void startAuction_shouldThrowWhenBeforeStartTime() throws InvalidBidException {
    Auction auction = auctionService.createAuction(
            item,
            LocalDateTime.now().plusMinutes(10),
            LocalDateTime.now().plusHours(1)
    );

    assertThrows(
            AuctionClosedException.class,
            () -> auctionService.startAuction(auction)
    );
}

@Test
void placeBid_shouldThrowWhenAuctionIsNull() {
    assertThrows(
            InvalidBidException.class,
            () -> auctionService.placeBid(null, bidder, 12_000_000d)
    );
}

@Test
void placeBid_shouldThrowWhenBidderIsNull() throws InvalidBidException, AuctionClosedException {
    Auction auction = createRunningAuction();

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.placeBid(auction, null, 12_000_000d)
    );
}

@Test
void placeBid_shouldThrowWhenAuctionIsOpen() throws InvalidBidException {
    Auction auction = auctionService.createAuction(
            item,
            LocalDateTime.now().plusMinutes(10),
            LocalDateTime.now().plusHours(1)
    );

    assertThrows(
            AuctionClosedException.class,
            () -> auctionService.placeBid(auction, bidder, 12_000_000d)
    );
}

@Test
void placeBid_shouldThrowWhenAuctionIsCancelled() throws InvalidBidException, AuctionClosedException {
    Auction auction = createRunningAuction();
    auction.setStatus(AuctionStatus.CANCELLED);

    assertThrows(
            AuctionClosedException.class,
            () -> auctionService.placeBid(auction, bidder, 12_000_000d)
    );
}

@Test
void placeBid_shouldThrowWhenAuctionIsFinished() throws InvalidBidException {
    Auction auction = auctionService.createAuction(
            item,
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusHours(1)
    );

    assertThrows(
            AuctionClosedException.class,
            () -> auctionService.placeBid(auction, bidder, 12_000_000d)
    );
}

@Test
void placeBid_shouldThrowWhenBidAmountEqualsCurrentPrice()
        throws InvalidBidException, AuctionClosedException {

    Auction auction = createRunningAuction();
    double equalAmount = auction.getCurrentPrice();

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.placeBid(auction, bidder, equalAmount)
    );
}

@Test
void placeBid_shouldThrowWhenBidAmountLowerThanCurrentPrice()
        throws InvalidBidException, AuctionClosedException {

    Auction auction = createRunningAuction();
    double lowerAmount = auction.getCurrentPrice() - 1_000d;

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.placeBid(auction, bidder, lowerAmount)
    );
}

@Test
void placeBid_shouldThrowWhenAuctionHasEndedByTime() throws InvalidBidException {
    Auction auction = auctionService.createAuction(
            item,
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusMinutes(1)
    );

    assertThrows(
            AuctionClosedException.class,
            () -> auctionService.placeBid(auction, bidder, 12_000_000d)
    );
}

@Test
void finishAuction_shouldThrowWhenAuctionIsNull() {
    assertThrows(
            InvalidBidException.class,
            () -> auctionService.finishAuction(null)
    );
}

@Test
void finishAuction_shouldThrowWhenAuctionHasInvalidTimeRange() {
    Auction auction = new Auction(item,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusMinutes(5));

    assertThrows(
            InvalidBidException.class,
            () -> auctionService.finishAuction(auction)
    );
}

@Test
void finishAuction_shouldReturnSilentlyWhenAlreadyFinished() throws InvalidBidException {
    Auction auction = auctionService.createAuction(
            item,
            LocalDateTime.now().minusMinutes(10),
            LocalDateTime.now().plusMinutes(30)
    );
    auction.setStatus(AuctionStatus.FINISHED);

    assertDoesNotThrow(() -> auctionService.finishAuction(auction));
    assertEquals(AuctionStatus.FINISHED, auction.getStatus());
}

@Test
void finishAuction_shouldReturnSilentlyWhenCancelled() throws InvalidBidException {
    Auction auction = auctionService.createAuction(
            item,
            LocalDateTime.now().minusMinutes(10),
            LocalDateTime.now().plusMinutes(30)
    );
    auction.setStatus(AuctionStatus.CANCELLED);

    assertDoesNotThrow(() -> auctionService.finishAuction(auction));
    assertEquals(AuctionStatus.CANCELLED, auction.getStatus());
}

@Test
void cancelAuction_shouldThrowWhenAuctionIsNull() {
    assertThrows(
            InvalidBidException.class,
            () -> auctionService.cancelAuction(null)
    );
}

@Test
void cancelAuction_shouldThrowWhenAuctionAlreadyFinished() throws InvalidBidException {
    Auction auction = auctionService.createAuction(
            item,
            LocalDateTime.now().minusMinutes(10),
            LocalDateTime.now().plusMinutes(30)
    );
    auction.setStatus(AuctionStatus.FINISHED);

    assertThrows(
            AuctionClosedException.class,
            () -> auctionService.cancelAuction(auction)
    );
}

    private Auction createRunningAuction()
            throws InvalidBidException, AuctionClosedException {

        Auction auction = auctionService.createAuction(
                item,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().plusMinutes(30)
        );

        auctionService.startAuction(auction);
        return auction;
    }

    private Auction createRunningAuctionDirectly() throws InvalidBidException {
        Auction auction = auctionService.createAuction(
                item,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().plusMinutes(30)
        );
        auction.setStatus(AuctionStatus.RUNNING);
        return auction;
    }

    /**
     * Concrete test double for abstract Item.
     */
    private static class TestItem extends Item {
        public TestItem(String name, String description, double startingPrice) {
            super(name, description, startingPrice);
        }

        @Override
        public String getCategory() {
            return "TEST";
        }
    }
}