package ddc.client.network.response;

import java.util.List;
import ddc.client.model.NotificationDTO;

public class NotificationListResponse extends BaseResponse {
    private List<NotificationDTO> notifications;
    private int unreadCount;

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
