package ddc.client.network.response;

import ddc.client.model.UserDTO;

public class UserListResponse extends Response<UserListResponse> {
    private UserDTO[] data;

    public UserDTO[] getData() {
        return data;
    }

    public UserListResponse setData(UserDTO[] data) {
        this.data = data;
        return this;
    }
}
