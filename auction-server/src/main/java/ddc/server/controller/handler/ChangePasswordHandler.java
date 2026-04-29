package ddc.server.controller.handler;

import com.google.gson.Gson;
import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;
import ddc.server.controller.service.UserService;
import ddc.server.network.response.BaseResponse;
import ddc.server.model.user.User;

public class ChangePasswordHandler implements ActionHandler {
    // Sử dụng Service để xử lý logic và gọi DAO
    private final UserService userService = new UserService(); 
    private static final Gson gson = new Gson();

    @Override
    public Response handle(RequestMessage request) {
        try {
            System.out.println(">>> Server đang nhận CHANGE_PASSWORD...");
            
            // 1. Kiểm tra dữ liệu thô (Raw Data)
            if (request.getData() == null) {
                return new BaseResponse().setStatus("FAIL");
            }
            
            // 2. Parse từ JSON sang Object (UserDTO chứa username và mật khẩu mới)
            // Lưu ý: Đảm bảo Client gửi data dưới dạng JSON của UserDTO
            User userReq = gson.fromJson(request.getData(), User.class);
            
            System.out.println("> Dang doi mat khau cho User: " + userReq.getUsername());

            boolean isUpdated = userService.updatePassword(userReq.getUsername(), userReq.getPassword());
            
            // 4. Trả về kết quả
            if (isUpdated) {
                return new BaseResponse().setStatus("SUCCESS");
            } else {
                return new BaseResponse().setStatus("FAIL");
            }

        } catch (Throwable t) { 
            // Bắt mọi lỗi để tránh sập Server luồng này
            System.err.println("SERVER CRASHED TAI CHANGE_PASSWORD_HANDLER");
            t.printStackTrace(); 
            return new BaseResponse().setStatus("FAIL");
        }
    }
}