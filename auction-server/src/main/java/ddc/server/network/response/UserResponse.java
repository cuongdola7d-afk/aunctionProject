package ddc.server.network.response;

import ddc.server.model.user.User;

public class UserResponse extends Response<UserResponse> {
    private User data; // Đây là nơi chứa thông tin User trả về từ Server

    public User getData() {
        return data;
    }

    public UserResponse setData(User data) {
        this.data = data;
        return this;
    }
}