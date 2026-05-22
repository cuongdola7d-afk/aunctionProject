package ddc.server.controller.handler.admin;

import com.google.gson.JsonObject;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.handler.ActionHandler;
import ddc.server.controller.service.AdminService;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;

public class AdminDeleteUserHandler implements ActionHandler {
    private final AdminService adminService = new AdminService();

    @Override
    public Response handle(RequestMessage request) {
        JsonObject data = gson.fromJson(request.getData(), JsonObject.class);
        String adminUsername = getString(data, "adminUsername");
        String userId = getString(data, "userId");

        if (!adminService.isAdmin(adminUsername)) {
            return new BaseResponse().setStatus("FORBIDDEN").setMessage("Khong co quyen admin.");
        }

        boolean success = adminService.deleteUser(adminUsername, userId);
        return new BaseResponse()
                .setStatus(success ? "SUCCESS" : "FAILED")
                .setMessage(success ? "Da xoa user." : "Khong xoa duoc user.");
    }

    private String getString(JsonObject object, String key) {
        return object != null && object.has(key) && !object.get(key).isJsonNull()
                ? object.get(key).getAsString()
                : null;
    }
}
