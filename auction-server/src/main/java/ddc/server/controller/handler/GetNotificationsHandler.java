package ddc.server.controller.handler;

import com.google.gson.JsonObject;
import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.NotificationService;
import ddc.server.model.notification.Notification;
import ddc.server.network.response.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetNotificationsHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetNotificationsHandler.class);
    private final NotificationService notifService = new NotificationService();

    @Override
    public Response handle(RequestMessage request) {
        try {
            // data = {"userId":"...", "limit":20, "offset":0}
            JsonObject data = gson.fromJson(request.getData(), JsonObject.class);
            String userId = data.get("userId").getAsString();
            int limit = data.has("limit") ? data.get("limit").getAsInt() : 20;
            int offset = data.has("offset") ? data.get("offset").getAsInt() : 0;

            LOGGER.info("GET_NOTIFICATIONS: userId={}, limit={}, offset={}", userId, limit, offset);

            List<Notification> notifications = notifService.getNotifications(userId, limit, offset);
            int unread = notifService.getUnreadCount(userId);

            LOGGER.info("GET_NOTIFICATIONS: found {} notifications, {} unread", notifications.size(), unread);

            return new NotificationResponse()
                    .setStatus("SUCCESS")
                    .setNotifications(notifications)
                    .setUnreadCount(unread);
        } catch (Exception e) {
            LOGGER.error("Loi xu ly GET_NOTIFICATIONS", e);
            return new BaseResponse().setStatus("FAIL").setMessage("Loi lay notifications: " + e.getMessage());
        }
    }
}
