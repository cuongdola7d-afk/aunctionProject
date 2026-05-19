package ddc.server.controller.handler;

import com.google.gson.JsonObject;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.AdminService;
import ddc.server.network.response.AdminStatsResponse;
import ddc.server.network.response.Response;

public class AdminGetStatsHandler implements ActionHandler {
    private final AdminService adminService = new AdminService();

    @Override
    public Response handle(RequestMessage request) {
        String adminUsername = getString(request.getData(), "adminUsername");

        if (!adminService.isAdmin(adminUsername)) {
            return new AdminStatsResponse().setStatus("FORBIDDEN");
        }

        return new AdminStatsResponse()
                .setStatus("SUCCESS")
                .setData(adminService.getStats(adminUsername));
    }

    private String getString(String data, String key) {
        JsonObject object = gson.fromJson(data, JsonObject.class);
        return object != null && object.has(key) && !object.get(key).isJsonNull()
                ? object.get(key).getAsString()
                : null;
    }
}
