package ddc.server.controller.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.controller.RequestMessage;
import ddc.server.model.user.User;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;

public class ChangePasswordHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangePasswordHandler.class);

    @Override
    public Response handle(RequestMessage request) {
        try {
            LOGGER.info("Server đang nhận CHANGE_PASSWORD...");

            // 1. Kiểm tra dữ liệu thô (Raw Data)
            if (request.getData() == null) {
                return new BaseResponse().setStatus("FAIL");
            }

            // 2. Parse từ JSON sang Object (UserDTO chứa username và mật khẩu mới)
            // Lưu ý: Đảm bảo Client gửi data dưới dạng JSON của UserDTO
            User userReq = gson.fromJson(request.getData(), User.class);

            LOGGER.info("Dang doi mat khau cho User: {}", userReq.getUsername());

            boolean isUpdated = userService.updatePassword(userReq.getUsername(), userReq.getPassword());

            // 4. Trả về kết quả
            if (isUpdated) {
                return new BaseResponse().setStatus("SUCCESS");
            } else {
                return new BaseResponse().setStatus("FAIL");
            }

        } catch (Throwable t) {
            // Bắt mọi lỗi để tránh sập Server luồng này
            LOGGER.error("CHANGE_PASSWORD_HANDLER loi", t);
            return new BaseResponse().setStatus("FAIL");
        }
    }
}