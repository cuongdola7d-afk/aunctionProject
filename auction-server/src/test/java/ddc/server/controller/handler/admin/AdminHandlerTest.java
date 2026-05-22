package ddc.server.controller.handler.admin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

@DisplayName("Admin Handlers - Unit Tests")
class AdminHandlerTest {

    private final AdminGetUsersHandler getUsersHandler = new AdminGetUsersHandler();

    @Test
    @DisplayName("getUsers - Non-admin → FORBIDDEN")
    void testGetUsers_NonAdmin() {
        String json = "{\"adminUsername\":\"notadmin\"}";
        RequestMessage req = new RequestMessage("ADMIN_GET_USERS", json);
        Response resp = getUsersHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }

    @Test
    @DisplayName("getUsers - Null data → không crash")
    void testGetUsers_NullData() {
        RequestMessage req = new RequestMessage("ADMIN_GET_USERS", "{}");
        Response resp = getUsersHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // ==================== AdminGetStatsHandler ====================

    private final AdminGetStatsHandler getStatsHandler = new AdminGetStatsHandler();

    @Test
    @DisplayName("getStats - Non-admin → FORBIDDEN")
    void testGetStats_NonAdmin() {
        String json = "{\"adminUsername\":\"user123\"}";
        RequestMessage req = new RequestMessage("ADMIN_GET_STATS", json);
        Response resp = getStatsHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }

    @Test
    @DisplayName("getStats - Null adminUsername → FORBIDDEN")
    void testGetStats_NullUsername() {
        RequestMessage req = new RequestMessage("ADMIN_GET_STATS", "{}");
        Response resp = getStatsHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // ==================== AdminDeleteUserHandler ====================

    private final AdminDeleteUserHandler deleteUserHandler = new AdminDeleteUserHandler();

    @Test
    @DisplayName("deleteUser - Non-admin → FORBIDDEN")
    void testDeleteUser_NonAdmin() {
        String json = "{\"adminUsername\":\"notadmin\",\"userId\":\"U001\"}";
        RequestMessage req = new RequestMessage("ADMIN_DELETE_USER", json);
        Response resp = deleteUserHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }

    @Test
    @DisplayName("deleteUser - Null data → không crash")
    void testDeleteUser_NullData() {
        RequestMessage req = new RequestMessage("ADMIN_DELETE_USER", null);
        assertDoesNotThrow(() -> deleteUserHandler.handle(req));
    }

    // ==================== AdminUpdateUserStatusHandler ====================

    private final AdminUpdateUserStatusHandler updateStatusHandler = new AdminUpdateUserStatusHandler();

    @Test
    @DisplayName("updateUserStatus - Non-admin → FORBIDDEN")
    void testUpdateStatus_NonAdmin() {
        String json = "{\"adminUsername\":\"notadmin\",\"userId\":\"U001\",\"status\":\"BLOCKED\"}";
        RequestMessage req = new RequestMessage("ADMIN_UPDATE_USER_STATUS", json);
        Response resp = updateStatusHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }

    @Test
    @DisplayName("updateUserStatus - Null adminUsername → FORBIDDEN")
    void testUpdateStatus_NullAdmin() {
        RequestMessage req = new RequestMessage("ADMIN_UPDATE_USER_STATUS", "{}");
        Response resp = updateStatusHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }

    // ==================== AdminCancelAuctionHandler ====================

    private final AdminCancelAuctionHandler cancelAuctionHandler = new AdminCancelAuctionHandler();

    @Test
    @DisplayName("cancelAuction - Non-admin → FORBIDDEN")
    void testCancelAuction_NonAdmin() {
        String json = "{\"adminUsername\":\"notadmin\",\"auctionId\":\"A001\"}";
        RequestMessage req = new RequestMessage("ADMIN_CANCEL_AUCTION", json);
        Response resp = cancelAuctionHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }

    @Test
    @DisplayName("cancelAuction - Null data → không crash")
    void testCancelAuction_NullData() {
        RequestMessage req = new RequestMessage("ADMIN_CANCEL_AUCTION", null);
        assertDoesNotThrow(() -> cancelAuctionHandler.handle(req));
    }

    @Test
    @DisplayName("cancelAuction - Null auctionId với non-admin → FORBIDDEN")
    void testCancelAuction_NullAuctionId() {
        String json = "{\"adminUsername\":\"notadmin\"}";
        RequestMessage req = new RequestMessage("ADMIN_CANCEL_AUCTION", json);
        Response resp = cancelAuctionHandler.handle(req);
        assertEquals("FORBIDDEN", resp.getStatus());
    }
}
