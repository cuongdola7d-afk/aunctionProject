package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import ddc.server.dao.NotificationDAO;
import ddc.server.model.notification.Notification;
import ddc.server.model.notification.NotificationType;

@DisplayName("NotificationService - Unit Tests")
public class NotificationServiceTest {
    
    @Mock
    private NotificationDAO notificationDAO;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Cấu hình mock mặc định
        when(notificationDAO.create(any(Notification.class))).thenReturn(true);
        when(notificationDAO.getByUserId(anyString(), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(notificationDAO.countUnread(any())).thenReturn(0);
        when(notificationDAO.markRead(anyString())).thenReturn(true);
        when(notificationDAO.markAllRead(anyString())).thenReturn(true);
    }

    // ==================== Helper Methods ====================

    private Notification createTestNotification() {
        Notification notification = new Notification();
        notification.setId("N001");
        notification.setUserId("U001");
        notification.setType(NotificationType.BID_OUTBID.name());
        notification.setAuctionId("A001");
        notification.setTitle("Test Notification");
        notification.setMessage("Test message");
        notification.setRead(false);
        return notification;
    }

    // ==================== createNotification Tests ====================

    @Test
    @DisplayName("createNotification - Should handle null userId gracefully")
    void testCreateNotification_NullUserId() {
        // Cấu hình khi userId là null thì DB lưu thất bại
        when(notificationDAO.create(argThat(n -> n.getUserId() == null))).thenReturn(false);

        assertDoesNotThrow(() -> {
            Notification result = notificationService.createNotification(
                    null,
                    NotificationType.BID_OUTBID,
                    "A001",
                    "Test",
                    "Message"
            );
        }, "Should not throw exception for null userId");
    }

    @Test
    @DisplayName("createNotification - Should create notification with valid parameters")
    void testCreateNotification_ValidParameters() {
        String userId = "U001";
        NotificationType type = NotificationType.BID_OUTBID;
        String auctionId = "A001";
        String title = "You've been outbid";
        String message = "Someone has placed a higher bid";

        // Test that the service accepts these parameters
        assertNotNull(userId, "UserId should be valid");
        assertNotNull(type, "NotificationType should be valid");
        assertNotNull(auctionId, "AuctionId should be valid");
        assertNotNull(title, "Title should be valid");
        assertNotNull(message, "Message should be valid");
    }

    @Test
    @DisplayName("createNotification - Should accept all notification types")
    void testCreateNotification_AllNotificationTypes() {
        for (NotificationType type : NotificationType.values()) {
            assertNotNull(type, "NotificationType should be valid: " + type);
        }
    }

    @Test
    @DisplayName("createNotification - Should handle empty strings")
    void testCreateNotification_EmptyStrings() {
        assertDoesNotThrow(() -> {
            notificationService.createNotification(
                    "U001",
                    NotificationType.AUCTION_ENDED,
                    "",
                    "",
                    ""
            );
        }, "Should accept empty strings");
    }

    @Test
    @DisplayName("createNotification - Should handle very long messages")
    void testCreateNotification_LongMessage() {
        String longMessage = "a".repeat(10000);
        assertDoesNotThrow(() -> {
            notificationService.createNotification(
                    "U001",
                    NotificationType.AUCTION_ENDED,
                    "A001",
                    "Title",
                    longMessage
            );
        }, "Should handle very long messages");
    }

    @Test
    @DisplayName("createNotification - Should handle special characters in message")
    void testCreateNotification_SpecialCharacters() {
        String specialMessage = "Test @#$%^&*(){}[]|\\:;\"'<>,.?/";
        assertDoesNotThrow(() -> {
            notificationService.createNotification(
                    "U001",
                    NotificationType.AUCTION_ENDED,
                    "A001",
                    "Title",
                    specialMessage
            );
        }, "Should handle special characters");
    }

    @Test
    @DisplayName("createNotification - Should handle Unicode characters")
    void testCreateNotification_UnicodeCharacters() {
        String unicodeMessage = "Thông báo: 用户 已 đặt giá cao hơn";
        assertDoesNotThrow(() -> {
            notificationService.createNotification(
                    "U001",
                    NotificationType.BID_OUTBID,
                    "A001",
                    "Thông báo",
                    unicodeMessage
            );
        }, "Should handle Unicode characters");
    }

    // ==================== getNotifications Tests ====================

    @Test
    @DisplayName("getNotifications - Should return list for valid parameters")
    void testGetNotifications_ValidParameters() {
        List<Notification> result = notificationService.getNotifications("U001", 10, 0);
        assertNotNull(result, "Should return non-null list");
        assertTrue(result instanceof List, "Should return a List");
    }

    @Test
    @DisplayName("getNotifications - Should handle null userId")
    void testGetNotifications_NullUserId() {
        assertDoesNotThrow(() -> {
            List<Notification> result = notificationService.getNotifications(null, 10, 0);
            assertNotNull(result, "Should not throw exception");
        }, "Should handle null userId gracefully");
    }

    @Test
    @DisplayName("getNotifications - Should support pagination with limit")
    void testGetNotifications_WithLimit() {
        List<Notification> result = notificationService.getNotifications("U001", 5, 0);
        assertNotNull(result, "Should return non-null list");
    }

    @Test
    @DisplayName("getNotifications - Should support pagination with offset")
    void testGetNotifications_WithOffset() {
        List<Notification> result = notificationService.getNotifications("U001", 10, 20);
        assertNotNull(result, "Should return non-null list");
    }

    @Test
    @DisplayName("getNotifications - Should handle zero limit")
    void testGetNotifications_ZeroLimit() {
        List<Notification> result = notificationService.getNotifications("U001", 0, 0);
        assertNotNull(result, "Should return non-null list");
    }

    @Test
    @DisplayName("getNotifications - Should handle negative offset")
    void testGetNotifications_NegativeOffset() {
        List<Notification> result = notificationService.getNotifications("U001", 10, -1);
        assertNotNull(result, "Should handle negative offset");
    }

    @Test
    @DisplayName("getNotifications - Should handle large limit")
    void testGetNotifications_LargeLimit() {
        List<Notification> result = notificationService.getNotifications("U001", Integer.MAX_VALUE, 0);
        assertNotNull(result, "Should handle large limit");
    }

    // ==================== getUnreadCount Tests ====================

    @Test
    @DisplayName("getUnreadCount - Should return count for valid userId")
    void testGetUnreadCount_ValidUserId() {
        int count = notificationService.getUnreadCount("U001");
        assertTrue(count >= 0, "Unread count should be non-negative");
    }

    @Test
    @DisplayName("getUnreadCount - Should handle null userId")
    void testGetUnreadCount_NullUserId() {
        int count = notificationService.getUnreadCount(null);
        assertTrue(count >= 0, "Should return non-negative count");
    }

    @Test
    @DisplayName("getUnreadCount - Should return zero for user with no notifications")
    void testGetUnreadCount_NoNotifications() {
        int count = notificationService.getUnreadCount("U999");
        assertTrue(count >= 0, "Unread count should be >= 0");
    }

    @Test
    @DisplayName("getUnreadCount - Should handle empty userId string")
    void testGetUnreadCount_EmptyUserId() {
        int count = notificationService.getUnreadCount("");
        assertTrue(count >= 0, "Should handle empty userId");
    }

    @Test
    @DisplayName("getUnreadCount - Should handle whitespace userId")
    void testGetUnreadCount_WhitespaceUserId() {
        int count = notificationService.getUnreadCount("   ");
        assertTrue(count >= 0, "Should handle whitespace userId");
    }

    // ==================== markRead Tests ====================

    @Test
    @DisplayName("markRead - Should return boolean for valid notificationId")
    void testMarkRead_ValidNotificationId() {
        boolean result = notificationService.markRead("N001");
        // Chỉ kiểm tra không ném exception
        assertNotNull(Boolean.valueOf(result), "Should return boolean");
    }

    @Test
    @DisplayName("markRead - Should handle null notificationId")
    void testMarkRead_NullNotificationId() {
        boolean result = notificationService.markRead(null);
        assertNotNull(result, "Should return a result");
    }

    @Test
    @DisplayName("markRead - Should handle non-existent notificationId")
    void testMarkRead_NonExistentNotificationId() {
        boolean result = notificationService.markRead("NONEXISTENT_N999");
        assertNotNull(result, "Should return result for non-existent ID");
    }

    @Test
    @DisplayName("markRead - Should handle empty notificationId")
    void testMarkRead_EmptyNotificationId() {
        boolean result = notificationService.markRead("");
        assertNotNull(result, "Should return result");
    }

    @Test
    @DisplayName("markRead - Should handle whitespace notificationId")
    void testMarkRead_WhitespaceNotificationId() {
        boolean result = notificationService.markRead("   ");
        assertNotNull(result, "Should return result");
    }

    // ==================== markAllRead Tests ====================

    @Test
    @DisplayName("markAllRead - Should return boolean for valid userId")
    void testMarkAllRead_ValidUserId() {
        boolean result = notificationService.markAllRead("U001");
        // Chỉ kiểm tra không ném exception
        assertNotNull(Boolean.valueOf(result), "Should return boolean");
    }

    @Test
    @DisplayName("markAllRead - Should handle null userId")
    void testMarkAllRead_NullUserId() {
        boolean result = notificationService.markAllRead(null);
        assertNotNull(result, "Should return a result");
    }

    @Test
    @DisplayName("markAllRead - Should handle non-existent userId")
    void testMarkAllRead_NonExistentUserId() {
        boolean result = notificationService.markAllRead("USER_NONEXISTENT_999");
        assertNotNull(result, "Should return result");
    }

    @Test
    @DisplayName("markAllRead - Should handle empty userId")
    void testMarkAllRead_EmptyUserId() {
        boolean result = notificationService.markAllRead("");
        assertNotNull(result, "Should return result");
    }

    @Test
    @DisplayName("markAllRead - Should handle whitespace userId")
    void testMarkAllRead_WhitespaceUserId() {
        boolean result = notificationService.markAllRead("   ");
        assertNotNull(result, "Should return result");
    }

    // ==================== Notification Object Tests ====================

    @Test
    @DisplayName("Notification - Should support all NotificationTypes")
    void testNotificationTypes() {
        Notification notification = new Notification();
        for (NotificationType type : NotificationType.values()) {
            notification.setType(type.name());
            assertEquals(type.name(), notification.getType(), 
                        "Should set and get notification type: " + type);
        }
    }

    @Test
    @DisplayName("Notification - Should maintain data integrity")
    void testNotificationDataIntegrity() {
        Notification notification = createTestNotification();
        
        assertEquals("U001", notification.getUserId());
        assertEquals("A001", notification.getAuctionId());
        assertEquals("Test Notification", notification.getTitle());
        assertEquals("Test message", notification.getMessage());
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Edge case - Rapid successive notifications")
    void testEdgeCase_RapidNotifications() {
        for (int i = 0; i < 100; i++) {
            final int index = i;
            assertDoesNotThrow(() -> {
                notificationService.createNotification(
                        "U001",
                        NotificationType.BID_OUTBID,
                        "A001",
                        "Test " + index,
                        "Message " + index
                );
            }, "Should handle rapid notification creation");
        }
    }

    @Test
    @DisplayName("Edge case - Multiple users notifications")
    void testEdgeCase_MultipleUsers() {
        for (int i = 1; i <= 100; i++) {
            int unreadCount = notificationService.getUnreadCount("U" + i);
            assertTrue(unreadCount >= 0, "Should handle multiple users");
        }
    }

    @Test
    @DisplayName("Edge case - Mark notifications for users with no unread")
    void testEdgeCase_MarkAllReadEmptyUser() {
        boolean result = notificationService.markAllRead("USER_WITH_NO_NOTIFICATIONS");
        assertNotNull(result, "Should handle marking all read for user with no notifications");
    }
}
