package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("CreateAuctionHandler - Unit Tests")
class CreateAuctionHandlerTest {
    private final CreateAuctionHandler handler = new CreateAuctionHandler();

    @Test
    @DisplayName("createAuction - Null data → INVALID_INPUT")
    void testHandle_NullData() {
        RequestMessage req = new RequestMessage("CREATE_AUCTION", null);
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("createAuction - Blank data → INVALID_INPUT")
    void testHandle_BlankData() {
        RequestMessage req = new RequestMessage("CREATE_AUCTION", "   ");
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("createAuction - JSON lỗi → SERVER_ERROR")
    void testHandle_MalformedJson() {
        RequestMessage req = new RequestMessage("CREATE_AUCTION", "{BAD_JSON}");
        Response resp = handler.handle(req);
        assertEquals("SERVER_ERROR", resp.getStatus());
    }

    @Test
    @DisplayName("createAuction - Thiếu item → INVALID_INPUT hoặc SERVER_ERROR")
    void testHandle_NoItem() {
        String json = "{\"startTime\":\"2025-01-01T10:00\",\"endTime\":\"2025-01-01T12:00\",\"currentPrice\":1000}";
        RequestMessage req = new RequestMessage("CREATE_AUCTION", json);
        Response resp = handler.handle(req);
        // Không có item.id → INVALID_INPUT hoặc SERVER_ERROR (Gson parse LocalDateTime)
        assertNotEquals("SUCCESS", resp.getStatus());
    }

    @Test
    @DisplayName("createAuction - currentPrice <= 0 → INVALID_INPUT hoặc SERVER_ERROR")
    void testHandle_NonPositivePrice() {
        String json = "{\"item\":{\"id\":\"I001\"},\"startTime\":\"2025-01-01T10:00\","
                + "\"endTime\":\"2025-01-01T12:00\",\"currentPrice\":0}";
        RequestMessage req = new RequestMessage("CREATE_AUCTION", json);
        Response resp = handler.handle(req);
        assertNotEquals("SUCCESS", resp.getStatus());
    }
}
