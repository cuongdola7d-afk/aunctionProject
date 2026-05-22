package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.WalletService;
import ddc.server.network.request.WalletRequest;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.DepositResponse;
import ddc.server.network.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetWalletBalanceHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetWalletBalanceHandler.class);

    private final WalletService walletService;

    public GetWalletBalanceHandler() {
        this(new WalletService());
    }

    GetWalletBalanceHandler(WalletService walletService) {
        this.walletService = walletService;
    }

    @Override
    public Response handle(RequestMessage request) {
        try {
            if (request == null || isBlank(request.getData())) {
                return new BaseResponse()
                        .setStatus("INVALID_INPUT")
                        .setMessage("Thieu thong tin vi.");
            }

            WalletRequest walletRequest = gson.fromJson(request.getData(), WalletRequest.class);
            if (walletRequest == null || isBlank(walletRequest.getUserId())) {
                return new BaseResponse()
                        .setStatus("INVALID_INPUT")
                        .setMessage("Thong tin vi khong hop le.");
            }

            double balance = walletService.getBalance(walletRequest.getUserId().trim());
            DepositResponse response = new DepositResponse();
            response.setStatus("SUCCESS");
            response.setMessage(String.format("So du hien tai: %,.0f VND", balance));
            response.setBalance(balance);
            return response;
        } catch (Exception e) {
            LOGGER.error("GET_WALLET_BALANCE_HANDLER loi", e);
            return new BaseResponse()
                    .setStatus("FAIL")
                    .setMessage("Loi server khi lay so du vi.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
