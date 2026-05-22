package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("RegisterHandler - Unit Tests")
class RegisterHandlerTest {
    private final RegisterHandler handler = new RegisterHandler();

    @Test
    @DisplayName("register - Null data → INVALID_INPUT")
    void testHandle_NullData() {
        RequestMessage req = new RequestMessage("REGISTER", null);
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("register - JSON rỗng {} → INVALID_INPUT (thiếu field)")
    void testHandle_EmptyJson() {
        RequestMessage req = new RequestMessage("REGISTER", "{}");
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("register - Mật khẩu < 8 ký tự → PASSWORD_LESS_THAN_8")
    void testHandle_PasswordTooShort() {
        String json = "{\"username\":\"user1\",\"email\":\"user@test.com\",\"password\":\"1234\"}";
        RequestMessage req = new RequestMessage("REGISTER", json);
        Response resp = handler.handle(req);
        assertEquals("PASSWORD_LESS_THAN_8", resp.getStatus());
    }

    @Test
    @DisplayName("register - Email không hợp lệ → INVALID_EMAIL")
    void testHandle_InvalidEmail() {
        String json = "{\"username\":\"user1\",\"email\":\"invalidemail\",\"password\":\"password123\"}";
        RequestMessage req = new RequestMessage("REGISTER", json);
        Response resp = handler.handle(req);
        assertEquals("INVALID_EMAIL", resp.getStatus());
    }

    @Test
    @DisplayName("register - Email thiếu domain → INVALID_EMAIL")
    void testHandle_EmailMissingDomain() {
        String json = "{\"username\":\"user1\",\"email\":\"user@\",\"password\":\"password123\"}";
        RequestMessage req = new RequestMessage("REGISTER", json);
        Response resp = handler.handle(req);
        assertEquals("INVALID_EMAIL", resp.getStatus());
    }

    @Test
    @DisplayName("register - Thiếu email → INVALID_INPUT")
    void testHandle_MissingEmail() {
        String json = "{\"username\":\"user1\",\"password\":\"password123\"}";
        RequestMessage req = new RequestMessage("REGISTER", json);
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("register - Thiếu username → INVALID_INPUT")
    void testHandle_MissingUsername() {
        String json = "{\"email\":\"user@test.com\",\"password\":\"password123\"}";
        RequestMessage req = new RequestMessage("REGISTER", json);
        Response resp = handler.handle(req);
        assertEquals("INVALID_INPUT", resp.getStatus());
    }

    @Test
    @DisplayName("register - JSON lỗi format → ném JsonSyntaxException (handler không bắt)")
    void testHandle_MalformedJson() {
        RequestMessage req = new RequestMessage("REGISTER", "NOT_VALID_JSON");
        // Gson ném JsonSyntaxException khi parse non-JSON object
        assertThrows(Exception.class, () -> handler.handle(req));
    }
}
