package ddc.server.controller.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.dao.AuctionDAO;
import ddc.server.model.notification.NotificationType;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.network.client.RealtimeClientHandler;
import ddc.server.network.request.AuctionEventPayload;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;

// Scheduler quét auction hết hạn, cập nhật DB + broadcast
public class AuctionStatusScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionStatusScheduler.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "auction-status-scheduler");
        t.setDaemon(true); // tắt cùng JVM
        return t;
    });

    private final AuctionService auctionService;
    private final AuctionDAO auctionDAO = new AuctionDAO();
    private final NotificationService notifService = new NotificationService();

    public AuctionStatusScheduler(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // Bắt đầu quét mỗi intervalSeconds giây
    public void start(long intervalSeconds) {
        scheduler.scheduleAtFixedRate(this::scan, 5, intervalSeconds, TimeUnit.SECONDS);
        LOGGER.info("AuctionStatusScheduler started, interval={}s", intervalSeconds);
    }

    // Dừng scheduler
    public void stop() {
        scheduler.shutdown();
        LOGGER.info("AuctionStatusScheduler stopped.");
    }

    // Quét tất cả auction, refresh status, update DB + broadcast nếu đổi
    private void scan() {
        try {
            List<Auction> auctions = auctionDAO.getAllAuctions();

            for (Auction auction : auctions) {
                AuctionStatus oldStatus = auction.getStatus();

                // Bỏ qua auction đã kết thúc/hủy
                if (oldStatus == AuctionStatus.FINISHED || oldStatus == AuctionStatus.CANCELLED) {
                    continue;
                }

                auctionService.refreshAuctionStatus(auction);

                if (auction.getStatus() != oldStatus) {
                    // Dùng updateAuctionStatus thay vì updateAuction để tránh race condition
                    auctionDAO.updateAuctionStatus(auction.getId(), auction.getStatus());
                    LOGGER.info("Scheduler: {} -> {} (auction={})",
                            oldStatus, auction.getStatus(), auction.getId());

                    // Broadcast cho client đang subscribe
                    if (auction.getStatus() == AuctionStatus.FINISHED) {
                        broadcastFinished(auction);
                        createFinishedNotifications(auction);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Scheduler scan loi", e);
        }
    }

    // Gửi event FINISHED cho tất cả client đang theo dõi auction này
    private void broadcastFinished(Auction auction) {
        AuctionEventPayload event = new AuctionEventPayload();
        event.setEventType("FINISHED");
        event.setAuctionId(auction.getId());
        event.setCurrentPrice(auction.getCurrentPrice());
        event.setStatus(AuctionStatus.FINISHED.name());
        event.setEndTime(auction.getEndTime() != null ? auction.getEndTime().toString() : null);
        event.setMinBidIncrement(auction.getMinBidIncrement());

        if (auction.getHighestBidder() != null) {
            event.setBidderName(auction.getHighestBidder().getUsername());
        }

        event.setMessage("Phiên đấu giá đã kết thúc.");

        RealtimeClientHandler.broadcastAuctionEvent(auction.getId(), event);
    }

    private void createFinishedNotifications(Auction auction) {
        try {
            // Lấy sellerId từ sellerName qua UserDAO
            String sellerName = auction.getItem().getSellerName();
            String sellerId = null;
            if (sellerName != null && !sellerName.isBlank()) {
                User seller = new UserDAO().getUser(sellerName);
                if (seller != null) {
                    sellerId = seller.getId();
                }
            }
            String auctionName = auction.getItem().getItemName();
            if (auction.getHighestBidder() != null) {
                // Thông báo cho WINNER
                notifService.createNotification(
                        auction.getHighestBidder().getId(),
                        NotificationType.AUCTION_WON,
                        auction.getId(),
                        "Bạn đã thắng đấu giá!",
                        "Bạn thắng: " + auctionName + " với giá " + auction.getCurrentPrice());
                // Thông báo cho SELLER
                if (sellerId != null) {
                    notifService.createNotification(
                            sellerId,
                            NotificationType.AUCTION_ENDED,
                            auction.getId(),
                            "Phiên đấu giá kết thúc",
                            auctionName + " đã bán với giá " + auction.getCurrentPrice());
                }
            } else {
                // Không ai bid -> thông báo seller
                if (sellerId != null) {
                    notifService.createNotification(
                            sellerId,
                            NotificationType.AUCTION_NO_BID,
                            auction.getId(),
                            "Phiên đấu giá không có ai tham gia",
                            auctionName + " đã hết hạn mà không có bid nào.");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Loi tao notification khi auction ket thuc", e);
        }
    }
}
