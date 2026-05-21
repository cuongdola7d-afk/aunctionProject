package ddc.server.controller.service;

import ddc.server.dao.WalletDAO;

public class WalletService {
    // Tự tay gán cứng lệnh new ở đây
    private final WalletDAO walletDAO;

    public WalletService() {
        this(new WalletDAO());
    }

    WalletService(WalletDAO walletDAO) {
        this.walletDAO = walletDAO;
    }

    public double getBalance(String userId) {
        return walletDAO.getBalance(userId);
    }

    public boolean deposit(String userId, double amount) {
        if (amount <= 0) {
            return false;
        }
        return walletDAO.updateBalance(userId, amount, "DEPOSIT", "Nap tien vao tai khoan");
    }

    public boolean processAuctionFinished(String auctionId, String bidderId, String ownerId, double finalPrice) {
        if (finalPrice <= 0) {
            return false;
        }

        return walletDAO.transferBalance(
                bidderId,
                ownerId,
                finalPrice,
                "DEDUCT_BID",
                "Deduct auction winning amount: " + auctionId,
                "RECEIVE_MONEY",
                "Receive auction sale amount: " + auctionId);
    }
}
