package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;

public class LoginHandler implements ActionHandler {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public Response handle (RequestMessage request) {
        User requestUser = gson.fromJson(request.getData(), User.class);

        System.out.println("Login Checking: " + requestUser.getUsername());

        //Validity Checking
        if (requestUser.getPassword().length() < 8) {
            return new BaseResponse().setStatus("PASSWORD LESS THAN 8");
        }

        //Database Access
        User user = userDAO.loginUser(requestUser.getUsername(), requestUser.getPassword());

        if (user == null) {
            System.out.println("Account doesn't exist");
            return new BaseResponse().setStatus("UNAVAILABLE");
        }

        if (user.getPassword().equals(requestUser.getPassword())) {
            System.out.println("Success!");
            return new BaseResponse().setStatus("SUCCESS");
        } else {
            System.out.println(user);
            System.out.println("Wrong Password!");
            return new BaseResponse().setStatus("WRONG PASSWORD");
        }
    }
}
