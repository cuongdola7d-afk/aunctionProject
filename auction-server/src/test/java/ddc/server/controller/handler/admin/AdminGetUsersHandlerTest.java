package ddc.server.controller.handler.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("Admin Get Users Handler - Unit Tests")
class AdminGetUsersHandlerTest {

    private final AdminGetUsersHandler handler = new AdminGetUsersHandler();

    // Kiểm tra non-admin bị từ chối
    @Test
    @DisplayName("handle - non-admin → FORBIDDEN")
    void handle_nonAdmin_shouldReturnForbidden() {
        String json = "{\"adminUsername\":\"normaluser\"}";
        RequestMessage req = new RequestMessage("ADMIN_GET_USERS", json);

        Response resp = handler.handle(req);

        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // Kiểm tra null adminUsername → FORBIDDEN
    @Test
    @DisplayName("handle - null adminUsername → FORBIDDEN")
    void handle_nullAdmin_shouldReturnForbidden() {
        RequestMessage req = new RequestMessage("ADMIN_GET_USERS", "{}");

        Response resp = handler.handle(req);

        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // Kiểm tra adminUsername rỗng → FORBIDDEN
    @Test
    @DisplayName("handle - adminUsername rỗng → FORBIDDEN")
    void handle_emptyAdmin_shouldReturnForbidden() {
        String json = "{\"adminUsername\":\"\"}";
        RequestMessage req = new RequestMessage("ADMIN_GET_USERS", json);

        Response resp = handler.handle(req);

        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // Kiểm tra response không null
    @Test
    @DisplayName("handle - response luôn không null")
    void handle_shouldAlwaysReturnNonNullResponse() {
        String json = "{\"adminUsername\":\"notadmin\"}";
        RequestMessage req = new RequestMessage("ADMIN_GET_USERS", json);

        Response resp = handler.handle(req);

        assertNotNull(resp);
        assertNotNull(resp.getStatus());
    }
}
