package ddc.server.controller.service;

import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;

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

    public boolean updateUserProfile(User user) {
        // Bạn có thể thêm logic kiểm tra ở đây
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            return false;
        }
        
        return userDAO.updateUserProfile(user);
    }
}

