package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("MarkNotificationReadHandler - Unit Tests")
class MarkNotificationReadHandlerTest {
    private final MarkNotificationReadHandler handler = new MarkNotificationReadHandler();

    @Test
    @DisplayName("markRead - JSON lỗi → trả FAIL")
    void testHandle_MalformedJson() {
        RequestMessage req = new RequestMessage("MARK_NOTIFICATION_READ", "NOT_JSON");
        Response resp = handler.handle(req);
        assertEquals("FAIL", resp.getStatus());
    }

    @Test
    @DisplayName("markRead - Có notificationId → trả SUCCESS")
    void testHandle_WithNotificationId() {
        String json = "{\"notificationId\":\"N999\"}";
        RequestMessage req = new RequestMessage("MARK_NOTIFICATION_READ", json);
        Response resp = handler.handle(req);
        // markRead có thể false nhưng handler vẫn trả SUCCESS
        assertEquals("SUCCESS", resp.getStatus());
    }

    @Test
    @DisplayName("markRead - Có userId → mark all read, trả SUCCESS")
    void testHandle_WithUserId() {
        String json = "{\"userId\":\"U999\"}";
        RequestMessage req = new RequestMessage("MARK_NOTIFICATION_READ", json);
        Response resp = handler.handle(req);
        assertEquals("SUCCESS", resp.getStatus());
    }

    @Test
    @DisplayName("markRead - JSON rỗng {} → trả SUCCESS (không mark gì)")
    void testHandle_EmptyJson() {
        RequestMessage req = new RequestMessage("MARK_NOTIFICATION_READ", "{}");
        Response resp = handler.handle(req);
        assertEquals("SUCCESS", resp.getStatus());
    }
}
