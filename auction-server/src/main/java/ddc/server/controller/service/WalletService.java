package ddc.server.controller.service;

import ddc.server.dao.WalletDAO;
import ddc.server.exception.WalletException;
import ddc.server.exception.WalletException.InsufficientBalanceException;
import ddc.server.exception.WalletException.InvalidAmountException;
import ddc.server.exception.WalletException.OperationFailedException;

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
        try {
            depositOrThrow(userId, amount);
            return true;
        } catch (WalletException e) {
            return false;
        }
    }

    public void depositOrThrow(String userId, double amount) throws WalletException {
        validateUserId(userId);
        validatePositiveAmount(amount);

        boolean success = walletDAO.updateBalance(userId, amount, "DEPOSIT", "Nap tien vao tai khoan");
        if (!success) {
            throw new OperationFailedException("Nap tien that bai.");
        }
    }

    public boolean processAuctionFinished(String auctionId, String bidderId, String ownerId, double finalPrice) {
        try {
            processAuctionFinishedOrThrow(auctionId, bidderId, ownerId, finalPrice);
            return true;
        } catch (WalletException e) {
            return false;
        }
    }

    public void processAuctionFinishedOrThrow(String auctionId, String bidderId, String ownerId, double finalPrice)
            throws WalletException {
        validateUserId(bidderId);
        validateUserId(ownerId);
        validatePositiveAmount(finalPrice);

        if (getBalance(bidderId) < finalPrice) {
            throw new InsufficientBalanceException("So du vi khong du.");
        }

        boolean success = walletDAO.transferBalance(
                bidderId,
                ownerId,
                finalPrice,
                "DEDUCT_BID",
                "Deduct auction winning amount: " + auctionId,
                "RECEIVE_MONEY",
                "Receive auction sale amount: " + auctionId);
        if (!success) {
            throw new OperationFailedException("Xu ly thanh toan dau gia that bai.");
        }
    }

    private void validateUserId(String userId) throws WalletException {
        if (userId == null || userId.isBlank()) {
            throw new WalletException("UserId vi khong hop le.");
        }
    }

    private void validatePositiveAmount(double amount) throws InvalidAmountException {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new InvalidAmountException("So tien phai lon hon 0.");
        }
    }
}
