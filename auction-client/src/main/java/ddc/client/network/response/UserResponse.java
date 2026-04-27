package ddc.client.network.response;

import ddc.client.model.UserDTO;

public class UserResponse extends Response<UserResponse> {
    private UserDTO data; // Đây là nơi chứa thông tin User trả về từ Server

    public UserDTO getData() {
        return data;
    }

    public UserResponse setData(UserDTO data) {
        this.data = data;
        return this;
    }
}