package ddc.server.controller.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ddc.server.dao.NotificationDAO;
import ddc.server.model.notification.Notification;
import ddc.server.model.notification.NotificationType;

public class NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationDAO notificationDAO = new NotificationDAO();

    // Tạo notification và return nó (để push realtime nếu cần)
    public Notification createNotification(String userId, NotificationType type,
            String auctionId, String title, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type.name());
        n.setAuctionId(auctionId);
        n.setTitle(title);
        n.setMessage(message);

        if (notificationDAO.create(n)) {
            LOGGER.info("Notification created: type={}, userId={}", type, userId);
            // Broadcast realtime cho user
            int unreadCount = notificationDAO.countUnread(userId);
            ddc.server.network.client.RealtimeClientHandler.sendNotificationEventToUser(userId, unreadCount);
            return n;
        }
        LOGGER.warn("Failed to create notification: type={}, userId={}", type, userId);
        return null;
    }

    public List<Notification> getNotifications(String userId, int limit, int offset) {
        return notificationDAO.getByUserId(userId, limit, offset);
    }

    public int getUnreadCount(String userId) {
        return notificationDAO.countUnread(userId);
    }

    public boolean markRead(String notificationId) {
        return notificationDAO.markRead(notificationId);
    }

    public boolean markAllRead(String userId) {
        return notificationDAO.markAllRead(userId);
    }
}
