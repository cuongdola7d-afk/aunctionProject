
package ddc.server.controller.service;

import ddc.server.dao.WalletDAO;

public class WalletService {
    // Tự tay gán cứng lệnh new ở đây
    private final WalletDAO walletDAO;

    public WalletService() {
        this.walletDAO = new WalletDAO();
    }

    public double getBalance(String userId) {
        return walletDAO.getBalance(userId);
    }

    public boolean deposit(String userId, double amount) {
        if (amount <= 0) {
            return false;
        }
        return walletDAO.updateBalance(userId, amount, "DEPOSIT", "Nạp tiền vào tài khoản");
    }

    public boolean processAuctionFinished(String auctionId, String bidderId, String ownerId, double finalPrice) {
        double bidderBalance = walletDAO.getBalance(bidderId);
        if (bidderBalance < finalPrice) {
            return false;
        }

        boolean deductOk = walletDAO.updateBalance(bidderId, -finalPrice, "DEDUCT_BID", "Trừ tiền thắng đấu giá mã: " + auctionId);
        if (deductOk) {
            walletDAO.updateBalance(ownerId, finalPrice, "RECEIVE_MONEY", "Nhận tiền bán tài sản mã: " + auctionId);
            return true;
        }
        return false;
    }
}