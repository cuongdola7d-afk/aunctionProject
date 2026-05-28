package ddc.server.controller.service;

import ddc.server.dao.AdminDAO;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;

public class UserService {
    private final UserDAO userDAO;
    private final AdminDAO adminDAO;

    public UserService() {
        this(new UserDAO(), new AdminDAO());
    }

    UserService(UserDAO userDAO, AdminDAO adminDAO) {
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
    }
    
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

    public boolean deleteOwnAccount(String userId, String username) {
        if (userId == null || userId.isBlank() || username == null || username.isBlank()) {
            return false;
        }

        User existingUser = userDAO.getUserById(userId);
        if (existingUser == null || existingUser.getUsername() == null
                || !existingUser.getUsername().equals(username)) {
            return false;
        }

        return adminDAO.deleteUser(userId);
    }
}

