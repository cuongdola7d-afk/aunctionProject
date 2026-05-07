package ddc.server.controller.handler;

import com.google.gson.Gson;
import ddc.server.controller.RequestMessage;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;
import ddc.server.model.user.User;
import ddc.server.controller.service.UserService;

public class UpdateProfileHandler implements ActionHandler {
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

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
            System.err.println("SERVER CRASHED TAI UPDATE_PROFILE_HANDLER");
            e.printStackTrace(); 
            return new BaseResponse().setStatus("FAIL");
        }
    }
}