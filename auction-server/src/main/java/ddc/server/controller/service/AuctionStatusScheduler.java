package ddc.server.controller.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.dao.AuctionDAO;
import ddc.server.dao.UserDAO;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.user.User;
import ddc.server.network.client.RealtimeClientHandler;
import ddc.server.network.request.AuctionEventPayload;

// Scheduler quét auction hết hạn, cập nhật DB + broadcast
public class AuctionStatusScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionStatusScheduler.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "auction-status-scheduler");
        t.setDaemon(true);
        return t;
    });

    private final AuctionService auctionService;
    private final AuctionDAO auctionDAO = new AuctionDAO();
    private final WalletService walletService = new WalletService();

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
                        handleAuctionSettlement(auction);
                        broadcastFinished(auction);
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

        event.setMessage("Phien dau gia da ket thuc.");
        RealtimeClientHandler.broadcastAuctionEvent(auction.getId(), event);
    }

    private void handleAuctionSettlement(Auction auction) {
        LOGGER.info("Auction #{}: settlement check. highestBidder={}, sellerName={}, finalPrice={}",
                auction.getId(),
                auction.getHighestBidder() != null ? auction.getHighestBidder().getUsername() : null,
                auction.getItem() != null ? auction.getItem().getSellerName() : null,
                auction.getCurrentPrice());

        if (auction.getHighestBidder() == null) {
            LOGGER.info("Auction #{}: ended without bids.", auction.getId());
            return;
        }

        String winnerId = auction.getHighestBidder().getId();
        double finalPrice = auction.getCurrentPrice();
        String sellerName = auction.getItem() != null ? auction.getItem().getSellerName() : null;

        if (sellerName == null || sellerName.isBlank()) {
            LOGGER.error("Auction #{}: missing sellerName, cannot settle wallet.", auction.getId());
            return;
        }

        User owner = new UserDAO().getUser(sellerName);
        if (owner == null || owner.getId() == null || owner.getId().isBlank()) {
            LOGGER.error("Auction #{}: seller not found by sellerName={}", auction.getId(), sellerName);
            return;
        }

        LOGGER.info("Auction #{}: deduct winner wallet and credit seller wallet. winnerId={}, ownerId={}, finalPrice={}",
                auction.getId(), winnerId, owner.getId(), finalPrice);

        boolean success = walletService.processAuctionFinished(
                auction.getId(),
                winnerId,
                owner.getId(),
                finalPrice);

        if (success) {
            LOGGER.info("Auction #{}: wallet settlement success. winnerId={}, ownerId={}, finalPrice={}",
                    auction.getId(), winnerId, owner.getId(), finalPrice);
        } else {
            LOGGER.error("Auction #{}: wallet settlement failed. winnerId={}, ownerId={}, finalPrice={}",
                    auction.getId(), winnerId, owner.getId(), finalPrice);
        }
    }
}
