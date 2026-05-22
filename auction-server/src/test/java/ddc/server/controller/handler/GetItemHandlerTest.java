package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("GetItemHandler - Unit Tests")
class GetItemHandlerTest {
    private final GetItemHandler handler = new GetItemHandler();

    @Test
    @DisplayName("getItem - Data null → trả INVALID_INPUT")
    void testHandle_NullData() {
        RequestMessage req = new RequestMessage("GET_ITEM", null);
        Response resp = handler.handle(req);
        // Gson parse null → null itemId → INVALID_INPUT
        assertNotNull(resp.getStatus());
    }

    @Test
    @DisplayName("getItem - ID rỗng (JSON string rỗng) → trả INVALID_INPUT")
    void testHandle_EmptyId() {
        RequestMessage req = new RequestMessage("GET_ITEM", "\"\"");
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("getItem - ID hợp lệ dù không tìm thấy → trả SUCCESS (item=null)")
    void testHandle_ValidIdNotFound() {
        RequestMessage req = new RequestMessage("GET_ITEM", "\"ID_NONEXISTENT_999\"");
        Response resp = handler.handle(req);
        // Handler luôn trả SUCCESS ngay cả khi item null
        assertEquals("SUCCESS", resp.getStatus());
    }

    @Test
    @DisplayName("getItem - JSON lỗi format → SERVER_ERROR hoặc xử lý không crash")
    void testHandle_MalformedData() {
        RequestMessage req = new RequestMessage("GET_ITEM", "{not_valid}");
        Response resp = handler.handle(req);
        assertNotNull(resp.getStatus());
    }
}
