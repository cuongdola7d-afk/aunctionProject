package ddc.server.controller.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.dao.AuctionDAO;
import ddc.server.dao.UserDAO;
import ddc.server.dao.WalletDAO;
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
        t.setDaemon(true); // tắt cùng JVM
        return t;
    });

    private final AuctionService auctionService;
    private final AuctionDAO auctionDAO = new AuctionDAO();

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

        event.setMessage("Phiên đấu giá đã kết thúc.");

        RealtimeClientHandler.broadcastAuctionEvent(auction.getId(), event);
    }

    private void handleAuctionSettlement(Auction auction) {
        // 1. Kiểm tra xem có người ra giá nào không
        if (auction.getHighestBidder() == null) {
            LOGGER.info("Auction #{}: Kết thúc nhưng không có người đặt giá.", auction.getId());
            return;
        }

        // Lấy thông tin từ đối tượng auction
        String winnerId = auction.getHighestBidder().getId(); // Giả sử model có getId()
        double finalPrice = auction.getCurrentPrice();
        String sellerName = auction.getItem() != null ? auction.getItem().getSellerName() : null;

        if (sellerName == null || sellerName.isBlank()) {
            LOGGER.error("Auction #{}: Khong co sellerName tren item, khong the quyet toan tien.", auction.getId());
            return;
        }

        UserDAO userDAO = new UserDAO();
        User owner = userDAO.getUser(sellerName);
        if (owner == null || owner.getId() == null || owner.getId().isBlank()) {
            LOGGER.error("Auction #{}: Khong tim thay nguoi ban theo sellerName={}", auction.getId(), sellerName);
            return;
        }

        String ownerId = owner.getId();
        
        WalletDAO walletDAO = new WalletDAO();

        LOGGER.info("Auction #{}: Bắt đầu quyết toán tiền. Người thắng: {}, Số tiền: {}, Người bán: {}", 
                auction.getId(), winnerId, finalPrice, ownerId);

        // 2. Tiến hành TRỪ TIỀN người thắng cuộc
        // Truyền số tiền âm (-) để giảm số dư
        boolean deductSuccess = walletDAO.updateBalance(
            winnerId, 
            -finalPrice, 
            "DEDUCT_BID", 
            "Trừ tiền thắng đấu giá phiên #" + auction.getId()
        );

        if (!deductSuccess) {
            LOGGER.error("Auction #{}: Thất bại khi trừ tiền người thắng (userId={})", auction.getId(), winnerId);
            return; 
        }

        // 3. Tiến hành CỘNG TIỀN cho người bán (chủ sản phẩm)
        // Truyền số tiền dương (+) để tăng số dư
        boolean receiveSuccess = walletDAO.updateBalance(
            ownerId, 
            finalPrice, 
            "RECEIVE_MONEY", 
            "Nhận tiền bán sản phẩm từ phiên #" + auction.getId()
        );

        if (!receiveSuccess) {
            LOGGER.error("Auction #{}: Thất bại khi cộng tiền người bán (userId={})", auction.getId(), ownerId);
            // Lưu ý thực tế: Bạn có thể cần cơ chế hoàn tác tiền cho người thắng tại đây nếu cộng cho người bán lỗi
        } else {
            LOGGER.info("Auction #{}: Quyết toán tài chính thành công hoàn toàn.", auction.getId());
        }
    }
}

