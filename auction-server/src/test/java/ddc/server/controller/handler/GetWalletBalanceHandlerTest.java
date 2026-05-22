package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.WalletService;
import ddc.server.network.response.DepositResponse;
import ddc.server.network.response.Response;

class GetWalletBalanceHandlerTest {

    @Test
    void handle_shouldReturnInvalidInputWhenRequestIsNull() {
        GetWalletBalanceHandler handler = new GetWalletBalanceHandler(new FakeWalletService());

        Response response = handler.handle(null);

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenDataBlank() {
        GetWalletBalanceHandler handler = new GetWalletBalanceHandler(new FakeWalletService());

        Response response = handler.handle(new RequestMessage("GET_WALLET_BALANCE", " "));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenUserIdMissing() {
        GetWalletBalanceHandler handler = new GetWalletBalanceHandler(new FakeWalletService());

        Response response = handler.handle(request("""
                { "userId": " " }
                """));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnBalanceWhenInputIsValid() {
        FakeWalletService walletService = new FakeWalletService();
        walletService.balance = 250_000;
        GetWalletBalanceHandler handler = new GetWalletBalanceHandler(walletService);

        Response response = handler.handle(request("""
                { "userId": "  U001  " }
                """));

        DepositResponse depositResponse = assertInstanceOf(DepositResponse.class, response);
        assertEquals("SUCCESS", depositResponse.getStatus());
        assertEquals(250_000, depositResponse.getBalance());
        assertEquals("U001", walletService.balanceUserId);
    }

    @Test
    void handle_shouldReturnFailWhenServiceThrowsException() {
        FakeWalletService walletService = new FakeWalletService();
        walletService.throwOnGetBalance = true;
        GetWalletBalanceHandler handler = new GetWalletBalanceHandler(walletService);

        Response response = handler.handle(request("""
                { "userId": "U001" }
                """));

        assertEquals("FAIL", response.getStatus());
    }

    private RequestMessage request(String data) {
        return new RequestMessage("GET_WALLET_BALANCE", data);
    }

    private static class FakeWalletService extends WalletService {
        private boolean throwOnGetBalance;
        private double balance;
        private String balanceUserId;

        @Override
        public double getBalance(String userId) {
            if (throwOnGetBalance) {
                throw new RuntimeException("get balance failed");
            }
            balanceUserId = userId;
            return balance;
        }
    }
}
