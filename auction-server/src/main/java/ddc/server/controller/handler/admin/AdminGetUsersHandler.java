package ddc.server.controller.handler.admin;

import com.google.gson.JsonObject;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.handler.ActionHandler;
import ddc.server.controller.service.AdminService;
import ddc.server.network.response.Response;
import ddc.server.network.response.UserListResponse;

public class AdminGetUsersHandler implements ActionHandler {
    private final AdminService adminService = new AdminService();

    @Override
    public Response handle(RequestMessage request) {
        String adminUsername = getString(request.getData(), "adminUsername");

        if (!adminService.isAdmin(adminUsername)) {
            return new UserListResponse().setStatus("FORBIDDEN");
        }

        return new UserListResponse()
                .setStatus("SUCCESS")
                .setData(adminService.getAllUsers(adminUsername));
    }

    private String getString(String data, String key) {
        JsonObject object = gson.fromJson(data, JsonObject.class);
        return object != null && object.has(key) && !object.get(key).isJsonNull()
                ? object.get(key).getAsString()
                : null;
    }
}
