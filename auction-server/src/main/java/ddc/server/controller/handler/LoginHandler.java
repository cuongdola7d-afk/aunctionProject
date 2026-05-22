package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;
import ddc.server.network.response.UserResponse;

public class LoginHandler implements ActionHandler {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public Response<?> handle(RequestMessage request) {
        User requestUser = gson.fromJson(request.getData(), User.class);
        if (requestUser == null || isBlank(requestUser.getUsername()) || isBlank(requestUser.getPassword())) {
            return new BaseResponse().setStatus("INVALID_INPUT").setMessage("Thieu username hoac password.");
        }

        if (requestUser.getPassword().length() < 8) {
            return new BaseResponse().setStatus("PASSWORD_LESS_THAN_8");
        }

        User user = userDAO.getUser(requestUser.getUsername());
        if (user == null) {
            return new BaseResponse().setStatus("UNAVAILABLE");
        }

        if (!user.getPassword().equals(requestUser.getPassword())) {
            return new BaseResponse().setStatus("INVALID PASSWORD");
        }

        return new UserResponse()
                .setData(user)
                .setStatus("SUCCESS");    
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
