package ddc.server.controller.service;

import ddc.server.dao.UserDAO;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    
    public boolean updatePassword(String username, String newPassword) {
        // Bạn có thể thêm các bước kiểm tra logic tại đây
        // Ví dụ: kiểm tra độ dài mật khẩu tối thiểu ở phía Server lần nữa cho chắc
        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }

        // Gọi xuống DAO để thực thi câu lệnh SQL
        return userDAO.changePassword(username, newPassword);
    }
}
