package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;

@DisplayName("AuctionStatusScheduler - Unit Tests")
public class AuctionStatusSchedulerTest {

    private AuctionService auctionService;
    private AuctionStatusScheduler scheduler;

    @BeforeEach
    void setUp() {
        auctionService = new AuctionService();
        scheduler = new AuctionStatusScheduler(auctionService);
    }

    // ==================== Constructor & Lifecycle ====================

    @Test
    @DisplayName("constructor - Khởi tạo thành công với AuctionService hợp lệ")
    void testConstructor_ValidService() {
        assertNotNull(scheduler, "Scheduler phải được tạo thành công");
    }

    @Test
    @DisplayName("stop - Dừng scheduler không ném exception")
    void testStop_NoException() {
        assertDoesNotThrow(() -> scheduler.stop(), "stop() không được ném exception");
    }

    @Test
    @DisplayName("start & stop - Start rồi stop ngay lập tức không crash")
    void testStartAndStop_Immediately() {
        assertDoesNotThrow(() -> {
            scheduler.start(60);
            scheduler.stop();
        }, "start() → stop() không được crash");
    }

    // ==================== refreshAuctionStatus (qua AuctionService) ====================

    @Test
    @DisplayName("refreshStatus - Auction OPEN khi startTime trong tương lai")
    void testRefresh_FutureAuction_Open() {
        Auction auction = new Auction();
        auction.setStartTime(LocalDateTime.now().plusHours(2));
        auction.setEndTime(LocalDateTime.now().plusHours(4));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.OPEN, auction.getStatus(),
                "Auction chưa bắt đầu phải có status OPEN");
    }

    @Test
    @DisplayName("refreshStatus - Auction RUNNING khi đang trong khoảng thời gian")
    void testRefresh_ActiveAuction_Running() {
        Auction auction = new Auction();
        auction.setStartTime(LocalDateTime.now().minusMinutes(30));
        auction.setEndTime(LocalDateTime.now().plusHours(2));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.RUNNING, auction.getStatus(),
                "Auction đang diễn ra phải có status RUNNING");
    }

    @Test
    @DisplayName("refreshStatus - Auction FINISHED khi đã hết hạn")
    void testRefresh_ExpiredAuction_Finished() {
        Auction auction = new Auction();
        auction.setStartTime(LocalDateTime.now().minusHours(4));
        auction.setEndTime(LocalDateTime.now().minusHours(1));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.FINISHED, auction.getStatus(),
                "Auction đã hết hạn phải có status FINISHED");
    }

    @Test
    @DisplayName("refreshStatus - Auction CANCELLED không bị thay đổi status")
    void testRefresh_CancelledAuction_Unchanged() {
        Auction auction = new Auction();
        auction.setStatus("CANCELLED");
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(1));

        auctionService.refreshAuctionStatus(auction);

        assertEquals(AuctionStatus.CANCELLED, auction.getStatus(),
                "Auction CANCELLED không được tự động thay đổi status");
    }

    @Test
    @DisplayName("refreshStatus - Null auction không crash")
    void testRefresh_NullAuction_NoException() {
        assertDoesNotThrow(() -> auctionService.refreshAuctionStatus(null),
                "refreshAuctionStatus(null) không được ném exception");
    }

    // ==================== Scheduler lifecycle ====================

    @Test
    @DisplayName("start - Interval khác nhau không crash")
    void testStart_DifferentIntervals() {
        AuctionStatusScheduler s1 = new AuctionStatusScheduler(auctionService);
        AuctionStatusScheduler s2 = new AuctionStatusScheduler(auctionService);

        assertDoesNotThrow(() -> {
            s1.start(30);
            s2.start(120);
            s1.stop();
            s2.stop();
        }, "Nhiều scheduler với interval khác nhau không được crash");
    }
}
