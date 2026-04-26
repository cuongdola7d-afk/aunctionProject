package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;

public class RegisterHandler implements ActionHandler{
    private final UserDAO userDAO = new UserDAO();

    @Override
    public Response handle (RequestMessage request) {
        User requestUser = gson.fromJson(request.getData(), User.class);

        System.out.println("Register Checking: " + requestUser.getUsername());

        //Validity Checking
        if (requestUser.getPassword() == null || requestUser.getPassword().length() < 8) {
            return new BaseResponse().setStatus("PASSWORD LESS THAN 8");
        }

        //Database Access
        boolean isSuccess = userDAO.registerUser(requestUser);
        
        if (isSuccess) {
            System.out.println("Success!");
            return new BaseResponse().setStatus("SUCCESS");
        } else {
            System.out.println("Account Existed!");
            return new BaseResponse().setStatus("DUPLICATE");
        }
    }
}
