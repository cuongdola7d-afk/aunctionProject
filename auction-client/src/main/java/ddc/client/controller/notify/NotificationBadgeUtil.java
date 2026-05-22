package ddc.client.controller.notify;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ddc.client.config.GsonConfig;
import ddc.client.model.Request;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.NotificationListResponse;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class NotificationBadgeUtil {
    private static final Gson gson = GsonConfig.newGson();

    public static void setupBadge(Label badgeLabel) {
        if (badgeLabel == null) return;
        
        // Bind the text and visibility to UserSession's unreadCount
        badgeLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.valueOf(UserSession.getInstance().getUnreadCount()),
            UserSession.getInstance().unreadCountProperty()
        ));

        badgeLabel.visibleProperty().bind(Bindings.createBooleanBinding(
            () -> UserSession.getInstance().getUnreadCount() > 0,
            UserSession.getInstance().unreadCountProperty()
        ));

        // Fetch the initial count from server when this screen loads
        // (Just to make sure we are synced with DB)
        Thread.ofVirtual().start(() -> {
            JsonObject data = new JsonObject();
            data.addProperty("userId", UserSession.getInstance().getId());
            data.addProperty("limit", 1);
            data.addProperty("offset", 0);

            String json = RequestToServer.sendRequest(new Request().setAction("GET_NOTIFICATIONS").setData(data));
            NotificationListResponse resp = gson.fromJson(json, NotificationListResponse.class);

            if (resp != null && "SUCCESS".equals(resp.getStatus())) {
                Platform.runLater(() -> UserSession.getInstance().setUnreadCount(resp.getUnreadCount()));
            }
        });
    }
}
