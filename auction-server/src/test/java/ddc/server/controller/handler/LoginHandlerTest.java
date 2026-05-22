package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("LoginHandler - Unit Tests")
class LoginHandlerTest {
    private final LoginHandler handler = new LoginHandler();

    // JSON request không có username/password
    @Test
    @DisplayName("login - Null data trả INVALID_INPUT")
    void testHandle_NullData() {
        RequestMessage req = new RequestMessage("LOGIN", null);
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("login - JSON rỗng trả INVALID_INPUT")
    void testHandle_EmptyJson() {
        RequestMessage req = new RequestMessage("LOGIN", "{}");
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("login - Mật khẩu < 8 ký tự trả PASSWORD_LESS_THAN_8")
    void testHandle_PasswordTooShort() {
        String json = "{\"username\":\"user1\",\"password\":\"123\"}";
        RequestMessage req = new RequestMessage("LOGIN", json);
        Response resp = handler.handle(req);
        assertEquals("PASSWORD_LESS_THAN_8", resp.getStatus());
    }

    @Test
    @DisplayName("login - Thiếu username trả INVALID_INPUT")
    void testHandle_MissingUsername() {
        String json = "{\"password\":\"password123\"}";
        RequestMessage req = new RequestMessage("LOGIN", json);
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("login - User không tồn tại trả UNAVAILABLE")
    void testHandle_UserNotFound() {
        String json = "{\"username\":\"nonexistent_user_xyz\",\"password\":\"password123\"}";
        RequestMessage req = new RequestMessage("LOGIN", json);
        Response resp = handler.handle(req);
        assertEquals("UNAVAILABLE", resp.getStatus());
    }

    @Test
    @DisplayName("login - JSON lỗi format → ném JsonSyntaxException (handler không bắt)")
    void testHandle_MalformedJson() {
        RequestMessage req = new RequestMessage("LOGIN", "NOT_JSON_DATA");
        // Gson ném JsonSyntaxException khi parse non-JSON object string
        assertThrows(Exception.class, () -> handler.handle(req));
    }
}
