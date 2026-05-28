package ddc.server.controller.handler.admin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("Admin Delete User Handler - Unit Tests")
class AdminDeleteUserHandlerTest {

    private final AdminDeleteUserHandler handler = new AdminDeleteUserHandler();

    // Kiểm tra non-admin bị từ chối
    @Test
    @DisplayName("handle - non-admin → FORBIDDEN")
    void handle_nonAdmin_shouldReturnForbidden() {
        String json = "{\"adminUsername\":\"notadmin\",\"userId\":\"U001\"}";
        RequestMessage req = new RequestMessage("ADMIN_DELETE_USER", json);

        Response resp = handler.handle(req);

        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // Kiểm tra null adminUsername → FORBIDDEN
    @Test
    @DisplayName("handle - null adminUsername → FORBIDDEN")
    void handle_nullAdmin_shouldReturnForbidden() {
        String json = "{\"userId\":\"U001\"}";
        RequestMessage req = new RequestMessage("ADMIN_DELETE_USER", json);

        Response resp = handler.handle(req);

        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // Kiểm tra null data không crash
    @Test
    @DisplayName("handle - null data → không crash")
    void handle_nullData_shouldNotCrash() {
        RequestMessage req = new RequestMessage("ADMIN_DELETE_USER", null);
        assertDoesNotThrow(() -> handler.handle(req));
    }

    // Kiểm tra JSON rỗng → FORBIDDEN
    @Test
    @DisplayName("handle - JSON rỗng → FORBIDDEN")
    void handle_emptyJson_shouldReturnForbidden() {
        RequestMessage req = new RequestMessage("ADMIN_DELETE_USER", "{}");

        Response resp = handler.handle(req);

        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // Kiểm tra thiếu userId với non-admin → FORBIDDEN
    @Test
    @DisplayName("handle - thiếu userId → FORBIDDEN")
    void handle_missingUserId_shouldReturnForbidden() {
        String json = "{\"adminUsername\":\"notadmin\"}";
        RequestMessage req = new RequestMessage("ADMIN_DELETE_USER", json);

        Response resp = handler.handle(req);

        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // Kiểm tra response không null
    @Test
    @DisplayName("handle - response luôn không null")
    void handle_shouldAlwaysReturnNonNullResponse() {
        String json = "{\"adminUsername\":\"notadmin\",\"userId\":\"U001\"}";
        RequestMessage req = new RequestMessage("ADMIN_DELETE_USER", json);

        Response resp = handler.handle(req);

        assertNotNull(resp);
        assertNotNull(resp.getStatus());
    }
}
