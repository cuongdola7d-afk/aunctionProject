package ddc.server.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.network.message.MessageType;
import ddc.server.network.message.SocketMessage;

@DisplayName("SocketMessage & MessageType - Unit Tests")
class SocketMessageTest {

    // Kiểm tra constructor mặc định
    @Test
    @DisplayName("SocketMessage - constructor mặc định → null fields")
    void defaultConstructor_shouldHaveNullFields() {
        SocketMessage msg = new SocketMessage();
        assertNull(msg.getType());
        assertNull(msg.getPayloadJson());
    }

    // Kiểm tra constructor có tham số
    @Test
    @DisplayName("SocketMessage - constructor có tham số")
    void paramConstructor_shouldSetFields() {
        SocketMessage msg = new SocketMessage(MessageType.AUTH, "{\"userId\":\"U001\"}");

        assertEquals(MessageType.AUTH, msg.getType());
        assertEquals("{\"userId\":\"U001\"}", msg.getPayloadJson());
    }

    // Kiểm tra setters
    @Test
    @DisplayName("SocketMessage - setters hoạt động đúng")
    void setters_shouldUpdateFields() {
        SocketMessage msg = new SocketMessage();
        msg.setType(MessageType.PLACE_BID);
        msg.setPayloadJson("{\"amount\":1000}");

        assertEquals(MessageType.PLACE_BID, msg.getType());
        assertEquals("{\"amount\":1000}", msg.getPayloadJson());
    }

    // Kiểm tra tất cả enum values của MessageType
    @Test
    @DisplayName("MessageType - enum có đủ các giá trị")
    void messageType_shouldHaveAllValues() {
        MessageType[] values = MessageType.values();

        // Phải có đủ 9 loại message
        assertEquals(9, values.length);

        assertNotNull(MessageType.valueOf("AUTH"));
        assertNotNull(MessageType.valueOf("SUBSCRIBE_AUCTION"));
        assertNotNull(MessageType.valueOf("PLACE_BID"));
        assertNotNull(MessageType.valueOf("AUCTION_EVENT"));
        assertNotNull(MessageType.valueOf("NOTIFICATION_EVENT"));
        assertNotNull(MessageType.valueOf("DASHBOARD_UPDATE"));
        assertNotNull(MessageType.valueOf("DASHBOARD_REFRESH"));
        assertNotNull(MessageType.valueOf("ERROR"));
        assertNotNull(MessageType.valueOf("PING"));
    }

    // Kiểm tra set null values
    @Test
    @DisplayName("SocketMessage - set null values")
    void setNullValues_shouldBeAllowed() {
        SocketMessage msg = new SocketMessage(MessageType.AUTH, "payload");
        msg.setType(null);
        msg.setPayloadJson(null);

        assertNull(msg.getType());
        assertNull(msg.getPayloadJson());
    }

    // Kiểm tra ghi đè giá trị
    @Test
    @DisplayName("SocketMessage - ghi đè giá trị cũ")
    void setters_shouldOverrideValues() {
        SocketMessage msg = new SocketMessage(MessageType.AUTH, "old");

        msg.setType(MessageType.PING);
        msg.setPayloadJson("new");

        assertEquals(MessageType.PING, msg.getType());
        assertEquals("new", msg.getPayloadJson());
    }

    // Kiểm tra payload JSON rỗng
    @Test
    @DisplayName("SocketMessage - payload JSON rỗng")
    void emptyPayload_shouldBeAllowed() {
        SocketMessage msg = new SocketMessage(MessageType.PING, "");
        assertEquals("", msg.getPayloadJson());
    }
}
