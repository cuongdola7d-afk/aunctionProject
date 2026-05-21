package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.WalletService;
import ddc.server.network.response.DepositResponse;
import ddc.server.network.response.Response;

class DepositHandlerTest {

    @Test
    void handle_shouldReturnInvalidInputWhenRequestIsNull() {
        DepositHandler handler = new DepositHandler(new FakeWalletService());

        Response<?> response = handler.handle(null);

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenDataBlank() {
        DepositHandler handler = new DepositHandler(new FakeWalletService());

        Response<?> response = handler.handle(new RequestMessage("DEPOSIT", " "));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenUserIdMissing() {
        DepositHandler handler = new DepositHandler(new FakeWalletService());

        Response<?> response = handler.handle(request("""
                { "amount": 100000 }
                """));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenAmountIsNotPositive() {
        DepositHandler handler = new DepositHandler(new FakeWalletService());

        Response<?> response = handler.handle(request("""
                { "userId": "U001", "amount": 0 }
                """));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnFailWhenServiceDepositFails() {
        FakeWalletService walletService = new FakeWalletService();
        walletService.depositResult = false;
        DepositHandler handler = new DepositHandler(walletService);

        Response<?> response = handler.handle(request("""
                { "userId": "U001", "amount": 100000 }
                """));

        assertEquals("FAIL", response.getStatus());
    }

    @Test
    void handle_shouldDepositAndReturnCurrentBalanceWhenInputIsValid() {
        FakeWalletService walletService = new FakeWalletService();
        walletService.balance = 150_000;
        DepositHandler handler = new DepositHandler(walletService);

        Response<?> response = handler.handle(request("""
                { "userId": "  U001  ", "amount": 100000 }
                """));

        DepositResponse depositResponse = assertInstanceOf(DepositResponse.class, response);
        assertEquals("SUCCESS", depositResponse.getStatus());
        assertEquals(150_000, depositResponse.getBalance());
        assertEquals("U001", walletService.depositUserId);
        assertEquals(100_000, walletService.depositAmount);
        assertEquals("U001", walletService.balanceUserId);
    }

    @Test
    void handle_shouldReturnFailWhenServiceThrowsException() {
        FakeWalletService walletService = new FakeWalletService();
        walletService.throwOnDeposit = true;
        DepositHandler handler = new DepositHandler(walletService);

        Response<?> response = handler.handle(request("""
                { "userId": "U001", "amount": 100000 }
                """));

        assertEquals("FAIL", response.getStatus());
    }

    private RequestMessage request(String data) {
        return new RequestMessage("DEPOSIT", data);
    }

    private static class FakeWalletService extends WalletService {
        private boolean depositResult = true;
        private boolean throwOnDeposit;
        private double balance;
        private String depositUserId;
        private double depositAmount;
        private String balanceUserId;

        @Override
        public boolean deposit(String userId, double amount) {
            if (throwOnDeposit) {
                throw new RuntimeException("deposit failed");
            }
            depositUserId = userId;
            depositAmount = amount;
            return depositResult;
        }

        @Override
        public double getBalance(String userId) {
            balanceUserId = userId;
            return balance;
        }
    }
}
