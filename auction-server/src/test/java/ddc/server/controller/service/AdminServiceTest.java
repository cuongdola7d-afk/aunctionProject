package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminServiceTest {
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService();
    }

    @Test
    void isAdmin_shouldRejectNullOrBlankUsername() {
        assertFalse(adminService.isAdmin(null));
        assertFalse(adminService.isAdmin(""));
        assertFalse(adminService.isAdmin("   "));
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenAdminUsernameInvalid() {
        assertTrue(adminService.getAllUsers(null).isEmpty());
        assertTrue(adminService.getAllUsers("").isEmpty());
    }

    @Test
    void getStats_shouldReturnEmptyMapWhenAdminUsernameInvalid() {
        assertTrue(adminService.getStats(null).isEmpty());
        assertTrue(adminService.getStats("").isEmpty());
    }

    @Test
    void deleteUser_shouldRejectInvalidInputBeforeDeleting() {
        assertFalse(adminService.deleteUser(null, "U001"));
        assertFalse(adminService.deleteUser("", "U001"));
    }

    @Test
    void updateUserStatus_shouldRejectInvalidInputBeforeUpdating() {
        assertFalse(adminService.updateUserStatus(null, "U001", "BLOCKED"));
        assertFalse(adminService.updateUserStatus("", "U001", "BLOCKED"));
    }

    @Test
    void cancelAuction_shouldRejectInvalidInputBeforeCancelling() {
        assertFalse(adminService.cancelAuction(null, "A001"));
        assertFalse(adminService.cancelAuction("", "A001"));
    }
}
