package ddc.server.network.response;

import java.util.List;

import ddc.server.model.user.User;

public class UserListResponse extends Response<UserListResponse> {
    private List<User> data;

    public List<User> getData() {
        return data;
    }

    public UserListResponse setData(List<User> data) {
        this.data = data;
        return this;
    }
}
