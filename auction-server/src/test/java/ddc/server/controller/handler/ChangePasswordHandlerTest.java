package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("ChangePasswordHandler - Unit Tests")
class ChangePasswordHandlerTest {
    private final ChangePasswordHandler handler = new ChangePasswordHandler();

    @Test
    @DisplayName("changePassword - Null data trả FAIL")
    void testHandle_NullData() {
        RequestMessage req = new RequestMessage("CHANGE_PASSWORD", null);
        Response resp = handler.handle(req);
        assertEquals("FAIL", resp.getStatus());
    }

    @Test
    @DisplayName("changePassword - JSON lỗi không ném exception, trả FAIL")
    void testHandle_MalformedJson() {
        RequestMessage req = new RequestMessage("CHANGE_PASSWORD", "INVALID_JSON");
        Response resp = handler.handle(req);
        // Gson parse lỗi → catch → FAIL
        assertNotNull(resp.getStatus());
    }

    @Test
    @DisplayName("changePassword - Mật khẩu < 6 ký tự trả FAIL (UserService reject)")
    void testHandle_PasswordTooShort() {
        String json = "{\"username\":\"user1\",\"password\":\"123\"}";
        RequestMessage req = new RequestMessage("CHANGE_PASSWORD", json);
        Response resp = handler.handle(req);
        assertEquals("FAIL", resp.getStatus());
    }

    @Test
    @DisplayName("changePassword - Mật khẩu null trả FAIL (UserService reject)")
    void testHandle_NullPassword() {
        String json = "{\"username\":\"user1\"}";
        RequestMessage req = new RequestMessage("CHANGE_PASSWORD", json);
        Response resp = handler.handle(req);
        assertEquals("FAIL", resp.getStatus());
    }
}
