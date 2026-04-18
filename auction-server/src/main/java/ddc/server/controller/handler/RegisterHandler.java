package ddc.server.controller.handler;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;

public class RegisterHandler implements ActionHandler{
    private final Gson gson = GsonConfig.newGson();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public String handle (RequestMessage request) {
        User requestUser = gson.fromJson(request.getData(), User.class);

        System.out.println("Register Checking: " + requestUser.getUsername());

        //Validity Checking
        if (requestUser.getPassword() == null || requestUser.getPassword().length() < 8) {
            return "\"PASSWORD LESS THAN 8\"";
        }

        //Database Access
        boolean isSuccess = userDAO.registerUser(requestUser);
        
        if (isSuccess) {
            System.out.println("Success!");
            return "\"SUCCESS\"";
        } else {
            System.out.println("Account Existed!");
            return "\"DUPLICATE\"";
        }
    }
}
