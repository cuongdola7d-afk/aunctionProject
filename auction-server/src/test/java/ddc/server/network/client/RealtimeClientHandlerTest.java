package ddc.server.network.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RealtimeClientHandler - Unit Tests")
class RealtimeClientHandlerTest {

    // Kiểm tra getActiveConnections trả về set không null
    @Test
    @DisplayName("getActiveConnections - trả về set không null")
    void getActiveConnections_shouldReturnNonNullSet() {
        var connections = RealtimeClientHandler.getActiveConnections();
        assertNotNull(connections);
    }

    // Kiểm tra isUserOnline với null userId → false
    @Test
    @DisplayName("isUserOnline - null userId → false")
    void isUserOnline_nullUserId_shouldReturnFalse() {
        assertFalse(RealtimeClientHandler.isUserOnline(null));
    }

    // Kiểm tra isUserOnline với userId rỗng → false
    @Test
    @DisplayName("isUserOnline - userId rỗng → false")
    void isUserOnline_emptyUserId_shouldReturnFalse() {
        assertFalse(RealtimeClientHandler.isUserOnline(""));
        assertFalse(RealtimeClientHandler.isUserOnline("   "));
    }

    // Kiểm tra isUserOnline với userId không tồn tại → false
    @Test
    @DisplayName("isUserOnline - userId không tồn tại → false")
    void isUserOnline_nonExistentUser_shouldReturnFalse() {
        assertFalse(RealtimeClientHandler.isUserOnline("nonexistent_user"));
    }

    // Kiểm tra broadcastAuctionEvent không crash với set rỗng
    @Test
    @DisplayName("broadcastAuctionEvent - không crash khi không có connection")
    void broadcastAuctionEvent_shouldNotCrashWithEmptyConnections() {
        assertDoesNotThrow(() ->
                RealtimeClientHandler.broadcastAuctionEvent("A001", new Object()));
    }

    // Kiểm tra broadcastDashboardUpdate không crash với set rỗng
    @Test
    @DisplayName("broadcastDashboardUpdate - không crash khi không có connection")
    void broadcastDashboardUpdate_shouldNotCrashWithEmptyConnections() {
        assertDoesNotThrow(() ->
                RealtimeClientHandler.broadcastDashboardUpdate("A001", 1000.0, "RUNNING", "2026-06-01T10:00:00"));
    }

    // Kiểm tra broadcastDashboardUpdate với null endTime
    @Test
    @DisplayName("broadcastDashboardUpdate - null endTime không crash")
    void broadcastDashboardUpdate_nullEndTime_shouldNotCrash() {
        assertDoesNotThrow(() ->
                RealtimeClientHandler.broadcastDashboardUpdate("A001", 500.0, "OPEN", null));
    }

    // Kiểm tra broadcastDashboardRefresh không crash
    @Test
    @DisplayName("broadcastDashboardRefresh - không crash khi không có connection")
    void broadcastDashboardRefresh_shouldNotCrashWithEmptyConnections() {
        assertDoesNotThrow(RealtimeClientHandler::broadcastDashboardRefresh);
    }

    // Kiểm tra sendNotificationEventToUser không crash với null userId
    @Test
    @DisplayName("sendNotificationEventToUser - null userId → không crash")
    void sendNotificationEventToUser_nullUserId_shouldNotCrash() {
        assertDoesNotThrow(() ->
                RealtimeClientHandler.sendNotificationEventToUser(null, 5));
    }

    // Kiểm tra sendNotificationEventToUser với userId không tồn tại
    @Test
    @DisplayName("sendNotificationEventToUser - userId không tồn tại → không crash")
    void sendNotificationEventToUser_nonExistentUser_shouldNotCrash() {
        assertDoesNotThrow(() ->
                RealtimeClientHandler.sendNotificationEventToUser("ghost_user", 10));
    }
}
