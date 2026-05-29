package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("DeleteAccountHandler - Unit Tests")
class DeleteAccountHandlerTest {

    @Test
    @DisplayName("handle - null data returns FAILED")
    void handle_nullData_shouldReturnFailed() {
        DeleteAccountHandler handler = new DeleteAccountHandler(new FakeUserService(false));

        Response response = handler.handle(new RequestMessage("DELETE_ACCOUNT", null));

        assertEquals("FAILED", response.getStatus());
    }

    @Test
    @DisplayName("handle - malformed JSON returns FAILED")
    void handle_malformedJson_shouldReturnFailed() {
        DeleteAccountHandler handler = new DeleteAccountHandler(new FakeUserService(false));

        Response response = handler.handle(new RequestMessage("DELETE_ACCOUNT", "INVALID_JSON"));

        assertEquals("FAILED", response.getStatus());
    }

    @Test
    @DisplayName("handle - service success returns SUCCESS")
    void handle_serviceSuccess_shouldReturnSuccess() {
        FakeUserService service = new FakeUserService(true);
        DeleteAccountHandler handler = new DeleteAccountHandler(service);
        String json = "{\"id\":\"U001\",\"username\":\"user1\"}";

        Response response = handler.handle(new RequestMessage("DELETE_ACCOUNT", json));

        assertEquals("SUCCESS", response.getStatus());
        assertTrue(service.called);
        assertEquals("U001", service.userId);
        assertEquals("user1", service.username);
    }

    @Test
    @DisplayName("handle - service failure returns FAILED")
    void handle_serviceFailure_shouldReturnFailed() {
        FakeUserService service = new FakeUserService(false);
        DeleteAccountHandler handler = new DeleteAccountHandler(service);
        String json = "{\"id\":\"U001\",\"username\":\"user1\"}";

        Response response = handler.handle(new RequestMessage("DELETE_ACCOUNT", json));

        assertEquals("FAILED", response.getStatus());
        assertTrue(service.called);
    }

    @Test
    @DisplayName("handle - missing id calls service and returns FAILED")
    void handle_missingId_shouldReturnFailed() {
        FakeUserService service = new FakeUserService(false);
        DeleteAccountHandler handler = new DeleteAccountHandler(service);
        String json = "{\"username\":\"user1\"}";

        Response response = handler.handle(new RequestMessage("DELETE_ACCOUNT", json));

        assertEquals("FAILED", response.getStatus());
        assertTrue(service.called);
        assertFalse(service.userId != null && service.userId.isBlank());
    }

    private static class FakeUserService implements DeleteAccountHandler.AccountDeletionService {
        private final boolean result;
        private boolean called;
        private String userId;
        private String username;

        FakeUserService(boolean result) {
            this.result = result;
        }

        public boolean deleteOwnAccount(String userId, String username) {
            this.called = true;
            this.userId = userId;
            this.username = username;
            return result;
        }
    }
}
