package ddc.server.controller.handler;

import com.google.gson.Gson;
import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;
import ddc.server.network.response.BaseResponse; 
import ddc.server.network.response.UserResponse; 
import ddc.server.network.response.Response;

public class LoginHandler implements ActionHandler {
    private final Gson gson = GsonConfig.newGson();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public Response<?> handle(RequestMessage request) { 
        User requestUser = gson.fromJson(request.getData(), User.class);
        System.out.println("Login Checking: " + requestUser.getUsername());

        // 1. Kiểm tra độ dài mật khẩu
        if (requestUser.getPassword().length() < 8) {
            return new BaseResponse().setStatus("PASSWORD_LESS_THAN_8");
        }

        // 2. Truy vấn Database
        User user = userDAO.loginUser(requestUser.getUsername(), requestUser.getPassword());

        if (user == null) {
            System.out.println("Account doesn't exist");
            return new BaseResponse().setStatus("UNAVAILABLE");
        }

        // 3. Kiểm tra mật khẩu và trả về dữ liệu
        if (user.getPassword().equals(requestUser.getPassword())) {
            System.out.println("Success!");
            
            // Trả về UserResponse kèm theo object user (đã lấy đủ id, name, email, phone từ DB)
            return new UserResponse()
                    .setData(user) 
                    .setStatus("SUCCESS");
        } else {
            System.out.println("Wrong Password!");
            return new BaseResponse().setStatus("WRONG_PASSWORD");
        }
    }
}