package ddc.server.controller.handler;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;

public class LoginHandler implements ActionHandler {
    private final Gson gson = GsonConfig.newGson();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public String handle (RequestMessage request) {
        User requestUser = gson.fromJson(request.getData(), User.class);

        System.out.println("Login Checking: " + requestUser.getUsername());

        //Validity Checking
        if (requestUser.getPassword().length() < 8) {
            return "\"PASSWORD LESS THAN 8\"";
        }

        //Database Access
        User user = userDAO.loginUser(requestUser.getUsername(), requestUser.getPassword());

        if (user == null) {
            System.out.println("Account doesn't exist");
            return "\"UNAVAILABLE\"";
        }

        if (user.getPassword().equals(requestUser.getPassword())) {
            System.out.println("Success!");
            return "SUCCESS:" + gson.toJson(user);
        } else {
            System.out.println(user);
            System.out.println("Wrong Password!");
            return "\"WRONG PASSWORD\"";
        }
    }
}
