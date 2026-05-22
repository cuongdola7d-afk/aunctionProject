package ddc.server.controller.handler;

import com.google.gson.JsonObject;
import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.NotificationService;
import ddc.server.network.response.*;

public class MarkNotificationReadHandler implements ActionHandler {
    private final NotificationService notifService = new NotificationService();

    @Override
    public Response handle(RequestMessage request) {
        try {
            JsonObject data = gson.fromJson(request.getData(), JsonObject.class);

            // Nếu có "notificationId" -> mark 1 cái, ngược lại mark all
            if (data.has("notificationId")) {
                notifService.markRead(data.get("notificationId").getAsString());
            } else if (data.has("userId")) {
                notifService.markAllRead(data.get("userId").getAsString());
            }

            return new BaseResponse().setStatus("SUCCESS").setMessage("Da danh dau doc.");
        } catch (Exception e) {
            return new BaseResponse().setStatus("FAIL").setMessage("Loi mark read.");
        }
    }
}
