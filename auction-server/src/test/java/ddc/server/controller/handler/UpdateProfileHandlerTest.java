package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("UpdateProfileHandler - Unit Tests")
class UpdateProfileHandlerTest {
    private final UpdateProfileHandler handler = new UpdateProfileHandler();

    @Test
    @DisplayName("updateProfile - Null data ném exception → trả FAIL")
    void testHandle_NullData() {
        RequestMessage req = new RequestMessage("UPDATE_PROFILE", null);
        Response resp = handler.handle(req);
        assertEquals("FAIL", resp.getStatus());
    }

    @Test
    @DisplayName("updateProfile - JSON lỗi → trả FAIL")
    void testHandle_MalformedJson() {
        RequestMessage req = new RequestMessage("UPDATE_PROFILE", "NOT_VALID_JSON");
        Response resp = handler.handle(req);
        assertNotNull(resp.getStatus());
    }

    @Test
    @DisplayName("updateProfile - Email không có @ → trả FAIL (UserService reject)")
    void testHandle_InvalidEmail() {
        String json = "{\"username\":\"user1\",\"email\":\"invalidemail\"}";
        RequestMessage req = new RequestMessage("UPDATE_PROFILE", json);
        Response resp = handler.handle(req);
        assertEquals("FAIL", resp.getStatus());
    }

    @Test
    @DisplayName("updateProfile - Null email → trả FAIL")
    void testHandle_NullEmail() {
        String json = "{\"username\":\"user1\",\"email\":null}";
        RequestMessage req = new RequestMessage("UPDATE_PROFILE", json);
        Response resp = handler.handle(req);
        assertEquals("FAIL", resp.getStatus());
    }
}
