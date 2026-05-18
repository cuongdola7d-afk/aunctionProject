package ddc.server.controller.service;

import java.util.List;
import java.util.Map;

import ddc.server.dao.AdminDAO;
import ddc.server.dao.UserDAO;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.User;

public class AdminService {
    private final AdminDAO adminDAO = new AdminDAO();
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
            Auction auction = new ddc.server.dao.AuctionDAO().getAuctionById(auctionId);
            auctionService.cancelAuction(auction);
            return new ddc.server.dao.AuctionDAO().updateAuction(auction);
        } catch (Exception e) {
            return false;
        }
    }
}
