package ddc.server.controller.service;

import java.util.List;
import java.util.Map;

import ddc.server.dao.AdminDAO;
import ddc.server.dao.AuctionDAO;
import ddc.server.dao.UserDAO;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.user.User;
import ddc.server.network.client.RealtimeClientHandler;
import ddc.server.network.request.AuctionEventPayload;
import ddc.server.pattern.Singleton.AuctionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    private final AdminDAO adminDAO = new AdminDAO();
    private final AuctionDAO auctionDAO = new AuctionDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AuctionService auctionService = new AuctionService();

    public boolean isAdmin(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        User user = userDAO.getUser(username);
        if (user == null) {
            return false;
        }

        return "ADMIN".equalsIgnoreCase(user.getRole()) || "admin".equalsIgnoreCase(user.getUsername());
    }

    public List<User> getAllUsers(String adminUsername) {
        if (!isAdmin(adminUsername)) {
            return List.of();
        }
        return adminDAO.getAllUsers();
    }

    public boolean deleteUser(String adminUsername, String userId) {
        if (!isAdmin(adminUsername) || userId == null || userId.isBlank()) {
            return false;
        }
        return adminDAO.deleteUser(userId);
    }

    public boolean updateUserStatus(String adminUsername, String userId, String status) {
        if (!isAdmin(adminUsername) || userId == null || userId.isBlank() || status == null || status.isBlank()) {
            return false;
        }
        if (!"ACTIVE".equalsIgnoreCase(status) && !"BLOCKED".equalsIgnoreCase(status)) {
            return false;
        }
        return adminDAO.updateUserStatus(userId, status);
    }

    public Map<String, Integer> getStats(String adminUsername) {
        if (!isAdmin(adminUsername)) {
            return Map.of();
        }
        return adminDAO.getStats();
    }

    public boolean cancelAuction(String adminUsername, String auctionId) {
        if (!isAdmin(adminUsername) || auctionId == null || auctionId.isBlank()) {
            return false;
        }

        try {
            Auction auction = auctionDAO.getAuctionById(auctionId);
            if (auction == null) {
                LOGGER.warn("Khong tim thay auction de huy: {}", auctionId);
                return false;
            }

            auctionService.cancelAuction(auction);
            boolean updated = auctionDAO.updateAuctionStatus(auctionId, AuctionStatus.CANCELLED);
            if (updated) {
                Auction cachedAuction = AuctionManager.getInstance().getAuction(auctionId);
                if (cachedAuction != null) {
                    cachedAuction.setStatus(AuctionStatus.CANCELLED.name());
                }
                broadcastAuctionCancelled(auction);
            }
            return updated;
        } catch (Exception e) {
            LOGGER.warn("Khong huy duoc auction: {}", auctionId, e);
            return false;
        }
    }

    private void broadcastAuctionCancelled(Auction auction) {
        AuctionEventPayload event = new AuctionEventPayload();
        event.setEventType("CANCELLED");
        event.setAuctionId(auction.getId());
        event.setCurrentPrice(auction.getCurrentPrice());
        event.setStatus(AuctionStatus.CANCELLED.name());
        event.setEndTime(auction.getEndTime() != null ? auction.getEndTime().toString() : null);
        event.setMinBidIncrement(auction.getMinBidIncrement());

        if (auction.getHighestBidder() != null) {
            event.setBidderName(auction.getHighestBidder().getUsername());
        }

        event.setMessage("Phien dau gia da bi huy.");
        RealtimeClientHandler.broadcastAuctionEvent(auction.getId(), event);
        RealtimeClientHandler.broadcastDashboardUpdate(auction.getId(),
                auction.getCurrentPrice(),
                AuctionStatus.CANCELLED.name(),
                auction.getEndTime() != null ? auction.getEndTime().toString() : null);
    }
}
