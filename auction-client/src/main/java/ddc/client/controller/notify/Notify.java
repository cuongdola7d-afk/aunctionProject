package ddc.client.controller.notify;

import com.google.gson.*;
import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.NotificationDTO;
import ddc.client.model.Request;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.NotificationListResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import javafx.animation.Transition;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Notify {
    private static final Gson gson = GsonConfig.newGson();

    @FXML
    private VBox notificationListVBox; // ← cần thêm vào FXML

    @FXML
    private Label badgeLabel;

    @FXML
    public void initialize() {
        NotificationBadgeUtil.setupBadge(badgeLabel);

        loadNotifications();
    }

    private void loadNotifications() {
        new Thread(() -> {
            JsonObject data = new JsonObject();
            data.addProperty("userId", UserSession.getInstance().getId());
            data.addProperty("limit", 20);
            data.addProperty("offset", 0);

            String json = RequestToServer.sendRequest(
                    new Request("GET_NOTIFICATIONS", data));

            NotificationListResponse resp = gson.fromJson(json, NotificationListResponse.class);

            Platform.runLater(() -> {
                notificationListVBox.getChildren().clear();

                if (resp == null || !"SUCCESS".equals(resp.getStatus())
                        || resp.getNotifications() == null || resp.getNotifications().isEmpty()) {
                    notificationListVBox.getChildren().add(new Label("Chưa có thông báo nào."));
                    return;
                }

                for (NotificationDTO n : resp.getNotifications()) {
                    notificationListVBox.getChildren().add(createNotifCard(n));
                }
            });
        }).start();
    }

    // Tạo 1 card cho mỗi notification
    private HBox createNotifCard(NotificationDTO n) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setFocusTraversable(false); // Tránh viền focus

        // Đặt màu nền ban đầu bằng Background thay vì CSS style
        if (n.isRead()) {
            card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        } else {
            card.setBackground(new Background(new BackgroundFill(Color.web("#bbdefb"), new CornerRadii(8), Insets.EMPTY)));
        }

        VBox content = new VBox(4);
        Label title = new Label(n.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label msg = new Label(n.getMessage());
        msg.setStyle("-fx-text-fill: #555;");

        Label time = new Label(n.getCreatedAt());
        time.setStyle("-fx-text-fill: #999; -fx-font-size: 11;");

        content.getChildren().addAll(title, msg, time);
        card.getChildren().add(content);

        // Click để đánh dấu đã đọc
        if (!n.isRead()) {
            card.setOnMouseClicked(e -> markRead(n.getId(), card));
        }

        return card;
    }

    // Hiệu ứng chuyển màu nền mượt mà
    private void animateBackground(HBox card, Color fromColor, Color toColor) {
        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(500));
            }
            @Override
            protected void interpolate(double frac) {
                Color color = fromColor.interpolate(toColor, frac);
                card.setBackground(new Background(new BackgroundFill(color, new CornerRadii(8), Insets.EMPTY)));
            }
        };
        transition.play();
    }

    private void markRead(String notifId, HBox card) {
        new Thread(() -> {
            try {
                JsonObject data = new JsonObject();
                data.addProperty("notificationId", notifId);
                RequestToServer.sendRequest(new Request("MARK_NOTIFICATION_READ", data));

                Platform.runLater(() -> animateBackground(card, Color.web("#bbdefb"), Color.WHITE));
            } catch (Exception ex) {
                System.err.println("Lỗi khi đánh dấu đã đọc thông báo: " + ex.getMessage());
            }
        }).start();
    }

    // Giữ nguyên các switch method hiện tại...
    @FXML
    @SuppressWarnings("unused")
    private void switchToHome(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToSelling(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToProfile(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToBidding(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }

}
