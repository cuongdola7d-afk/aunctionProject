package ddc.server.controller.handler;

import com.google.gson.Gson;
import ddc.server.controller.RequestMessage;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;
import ddc.server.model.user.User;
import ddc.server.controller.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateProfileHandler implements ActionHandler {
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProfileHandler.class);

    @Override
    public Response handle(RequestMessage request) {
        try {
            User updatedUser = gson.fromJson(request.getData().toString(), User.class);

            boolean success = userService.updateUserProfile(updatedUser);

            if (success) {
                return new BaseResponse().setStatus("SUCCESS");
            } else {
                return new BaseResponse().setStatus("FAIL");
            }
        } catch (Exception e) {
            LOGGER.error("UPDATE_PROFILE_HANDLER loi", e);
            return new BaseResponse().setStatus("FAIL");
        }
    }
}