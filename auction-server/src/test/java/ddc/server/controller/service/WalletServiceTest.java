package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ddc.server.dao.WalletDAO;
import ddc.server.exception.WalletException.InsufficientBalanceException;
import ddc.server.exception.WalletException.InvalidAmountException;
import ddc.server.exception.WalletException.OperationFailedException;

class WalletServiceTest {

    @Test
    void getBalance_shouldDelegateToDao() {
        FakeWalletDAO walletDAO = new FakeWalletDAO();
        walletDAO.balance = 120_000;
        WalletService service = new WalletService(walletDAO);

        double balance = service.getBalance("U001");

        assertEquals(120_000, balance);
        assertEquals("U001", walletDAO.lastBalanceUserId);
    }

    @Test
    void deposit_shouldRejectNonPositiveAmount() {
        FakeWalletDAO walletDAO = new FakeWalletDAO();
        WalletService service = new WalletService(walletDAO);

        assertFalse(service.deposit("U001", 0));
        assertFalse(service.deposit("U001", -1));
        assertEquals(0, walletDAO.updates.size());
    }

    @Test
    void deposit_shouldAddMoneyToUserWallet() {
        FakeWalletDAO walletDAO = new FakeWalletDAO();
        WalletService service = new WalletService(walletDAO);

        assertTrue(service.deposit("U001", 50_000));

        assertEquals(1, walletDAO.updates.size());
        WalletUpdate update = walletDAO.updates.get(0);
        assertEquals("U001", update.userId);
        assertEquals(50_000, update.amount);
        assertEquals("DEPOSIT", update.type);
    }

    @Test
    void depositOrThrow_shouldThrowInvalidAmountWhenAmountIsNotPositive() {
        WalletService service = new WalletService(new FakeWalletDAO());

        assertThrows(InvalidAmountException.class, () -> service.depositOrThrow("U001", 0));
    }

    @Test
    void depositOrThrow_shouldThrowOperationFailedWhenDaoFails() {
        FakeWalletDAO walletDAO = new FakeWalletDAO();
        walletDAO.nextUpdateResults.add(false);
        WalletService service = new WalletService(walletDAO);

        assertThrows(OperationFailedException.class, () -> service.depositOrThrow("U001", 50_000));
    }

    @Test
    void processAuctionFinished_shouldFailWhenBidderBalanceIsInsufficient() {
        FakeWalletDAO walletDAO = new FakeWalletDAO();
        walletDAO.balance = 99_999;
        WalletService service = new WalletService(walletDAO);

        boolean success = service.processAuctionFinished("A001", "BUYER", "SELLER", 100_000);

        assertFalse(success);
        assertEquals(0, walletDAO.updates.size());
    }

    @Test
    void processAuctionFinishedOrThrow_shouldThrowInsufficientBalanceWhenBalanceIsTooLow() {
        FakeWalletDAO walletDAO = new FakeWalletDAO();
        walletDAO.balance = 99_999;
        WalletService service = new WalletService(walletDAO);

        assertThrows(InsufficientBalanceException.class,
                () -> service.processAuctionFinishedOrThrow("A001", "BUYER", "SELLER", 100_000));
        assertEquals(0, walletDAO.updates.size());
    }

    @Test
    void processAuctionFinished_shouldFailAndNotMoveMoneyWhenTransferFails() {
        FakeWalletDAO walletDAO = new FakeWalletDAO();
        walletDAO.balance = 200_000;
        walletDAO.nextUpdateResults.add(false);
        WalletService service = new WalletService(walletDAO);

        boolean success = service.processAuctionFinished("A001", "BUYER", "SELLER", 100_000);

        assertFalse(success);
        assertEquals(0, walletDAO.updates.size());
    }

    @Test
    void processAuctionFinished_shouldDeductWinnerAndCreditSellerWhenDeductSucceeds() {
        FakeWalletDAO walletDAO = new FakeWalletDAO();
        walletDAO.balance = 200_000;
        WalletService service = new WalletService(walletDAO);

        boolean success = service.processAuctionFinished("A001", "BUYER", "SELLER", 100_000);

        assertTrue(success);
        assertEquals(2, walletDAO.updates.size());

        WalletUpdate deduct = walletDAO.updates.get(0);
        assertEquals("BUYER", deduct.userId);
        assertEquals(-100_000, deduct.amount);
        assertEquals("DEDUCT_BID", deduct.type);

        WalletUpdate credit = walletDAO.updates.get(1);
        assertEquals("SELLER", credit.userId);
        assertEquals(100_000, credit.amount);
        assertEquals("RECEIVE_MONEY", credit.type);
    }

    private static class FakeWalletDAO extends WalletDAO {
        private double balance;
        private String lastBalanceUserId;
        private final List<WalletUpdate> updates = new ArrayList<>();
        private final List<Boolean> nextUpdateResults = new ArrayList<>();

        @Override
        public double getBalance(String userId) {
            lastBalanceUserId = userId;
            return balance;
        }

        @Override
        public boolean updateBalance(String userId, double amount, String type, String description) {
            updates.add(new WalletUpdate(userId, amount, type, description));
            if (!nextUpdateResults.isEmpty()) {
                return nextUpdateResults.remove(0);
            }
            return true;
        }

        @Override
        public boolean transferBalance(String fromUserId, String toUserId, double amount,
                String deductType, String deductDescription,
                String receiveType, String receiveDescription) {
            if (amount <= 0 || balance < amount) {
                return false;
            }
            if (!nextUpdateResults.isEmpty() && !nextUpdateResults.remove(0)) {
                return false;
            }
            updates.add(new WalletUpdate(fromUserId, -amount, deductType, deductDescription));
            updates.add(new WalletUpdate(toUserId, amount, receiveType, receiveDescription));
            return true;
        }
    }

    private record WalletUpdate(String userId, double amount, String type, String description) {
    }
}
