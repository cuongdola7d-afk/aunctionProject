package ddc.server.controller.handler.admin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("GetAllAuctionHandler - Unit Tests")
class GetAllAuctionHandlerTest {
    private final GetAllAuctionHandler handler = new GetAllAuctionHandler();

    @Test
    @DisplayName("getAll - Không cần data vẫn trả kết quả không null")
    void testHandle_NoData() {
        RequestMessage req = new RequestMessage("GET_ALL_AUCTION", null);
        Response resp = handler.handle(req);
        assertNotNull(resp);
        assertNotNull(resp.getStatus());
    }

    @Test
    @DisplayName("getAll - Status luôn là SUCCESS hoặc FAIL, không crash")
    void testHandle_StatusIsValid() {
        RequestMessage req = new RequestMessage("GET_ALL_AUCTION", "");
        Response resp = handler.handle(req);
        assertTrue("SUCCESS".equals(resp.getStatus()) || "FAIL".equals(resp.getStatus()),
                "Status phải là SUCCESS hoặc FAIL");
    }
}
