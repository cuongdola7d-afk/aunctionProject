package ddc.server.controller.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParseException;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.UserService;
import ddc.server.model.user.User;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;

public class DeleteAccountHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAccountHandler.class);
    private final AccountDeletionService accountService;

    public DeleteAccountHandler() {
        UserService userService = new UserService();
        this.accountService = userService::deleteOwnAccount;
    }

    DeleteAccountHandler(AccountDeletionService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Response handle(RequestMessage request) {
        try {
            if (request.getData() == null) {
                return new BaseResponse().setStatus("FAILED").setMessage("Thieu thong tin tai khoan.");
            }

            User user = gson.fromJson(request.getData(), User.class);
            boolean success = accountService.deleteOwnAccount(user.getId(), user.getUsername());

            return new BaseResponse()
                    .setStatus(success ? "SUCCESS" : "FAILED")
                    .setMessage(success ? "Da xoa tai khoan." : "Khong xoa duoc tai khoan.");
        } catch (JsonParseException | IllegalStateException e) {
            return new BaseResponse().setStatus("FAILED").setMessage("Du lieu tai khoan khong hop le.");
        } catch (Exception e) {
            LOGGER.error("DELETE_ACCOUNT_HANDLER loi", e);
            return new BaseResponse().setStatus("FAILED").setMessage("Khong xoa duoc tai khoan.");
        }
    }

    interface AccountDeletionService {
        boolean deleteOwnAccount(String userId, String username);
    }
}
