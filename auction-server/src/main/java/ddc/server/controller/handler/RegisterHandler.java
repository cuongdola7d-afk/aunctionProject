package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;

public class RegisterHandler implements ActionHandler {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public Response handle(RequestMessage request) {
        User requestUser = gson.fromJson(request.getData(), User.class);
        if (requestUser == null
                || isBlank(requestUser.getUsername())
                || isBlank(requestUser.getEmail())
                || isBlank(requestUser.getPassword())) {
            return new BaseResponse().setStatus("INVALID_INPUT").setMessage("Thieu thong tin dang ky.");
        }

        if (requestUser.getPassword().length() < 8) {
            return new BaseResponse().setStatus("PASSWORD_LESS_THAN_8");
        }

        if (!requestUser.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return new BaseResponse().setStatus("INVALID_EMAIL");
        }

        boolean isSuccess = userDAO.registerUser(requestUser);
        if (isSuccess) {
            return new BaseResponse().setStatus("SUCCESS");
        }
        return new BaseResponse().setStatus("DUPLICATE");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
