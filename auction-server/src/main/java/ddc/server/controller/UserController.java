package ddc.server.controller;

import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;

public class UserController {

    private UserDAO userDAO = new UserDAO();

    public String handleRegister (User user) {

        System.out.println("Register Checking: " + user.getUsername());

        //Validity Checking
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            return "\"PASSWORD LESS THAN 8\"";
        }

        //Database Access
        boolean isSuccess = userDAO.registerUser(user);
        
        if (isSuccess) {
            System.out.println("Success!");
            return "\"SUCCESS\"";
        } else {
            System.out.println("Account Existed!");
            return "\"DUPLICATE\"";
        }
    }

    public String handleLogin (User user) {
        System.out.println("Login Checking: " + user.getUsername());

        //Validity Checking
        if (user.getPassword().length() < 8) {
            return "\"PASSWORD LESS THAN 8\"";
        }

        //Database Access
        User loginUser = userDAO.loginUser(user.getUsername(), user.getPassword());

        if (loginUser == null) {
            System.out.println("Account doesn't exist");
            return "\"UNAVAILABLE\"";
        }

        if (loginUser.getPassword().equals(user.getPassword())) {
            System.out.println("Success!");
            return "\"SUCCESS\"";
        } else {
            System.out.println(loginUser);
            System.out.println("Wrong Password!");
            return "\"WRONG PASSWORD\"";
        }
    }
}