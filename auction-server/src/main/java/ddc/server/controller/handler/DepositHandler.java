package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.WalletService;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepositHandler.class);

    private final WalletService walletService = new WalletService();

    @Override
    public Response handle(RequestMessage request) {
        try {
            if (request == null || isBlank(request.getData())) {
                return new BaseResponse()
                        .setStatus("INVALID_INPUT")
                        .setMessage("Thieu thong tin nap tien.");
            }

            DepositRequest depositRequest = gson.fromJson(request.getData(), DepositRequest.class);
            if (depositRequest == null
                    || isBlank(depositRequest.getUserId())
                    || !Double.isFinite(depositRequest.getAmount())
                    || depositRequest.getAmount() <= 0) {
                return new BaseResponse()
                        .setStatus("INVALID_INPUT")
                        .setMessage("Thong tin nap tien khong hop le.");
            }

            String userId = depositRequest.getUserId().trim();
            boolean success = walletService.deposit(userId, depositRequest.getAmount());
            if (!success) {
                return new BaseResponse()
                        .setStatus("FAIL")
                        .setMessage("Nap tien that bai.");
            }

            double balance = walletService.getBalance(userId);
            return new BaseResponse()
                    .setStatus("SUCCESS")
                    .setMessage(String.format("So du hien tai: %,.0f VND", balance));
        } catch (Exception e) {
            LOGGER.error("DEPOSIT_HANDLER loi", e);
            return new BaseResponse()
                    .setStatus("FAIL")
                    .setMessage("Loi server khi nap tien.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class DepositRequest {
        private String userId;
        private double amount;

        public String getUserId() {
            return userId;
        }

        public double getAmount() {
            return amount;
        }
    }
}
