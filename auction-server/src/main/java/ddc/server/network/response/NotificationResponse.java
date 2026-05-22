package ddc.server.network.response;

import java.util.List;
import ddc.server.model.notification.Notification;

public class NotificationResponse extends Response<NotificationResponse> {
    private List<Notification> notifications;
    private int unreadCount;
    private String message;

    // getter + setter cho cả 3 field
    public List<Notification> getNotifications() {
        return notifications;
    }

    public NotificationResponse setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        return this;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public NotificationResponse setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public NotificationResponse setMessage(String message) {
        this.message = message;
        return this;
    }

}
