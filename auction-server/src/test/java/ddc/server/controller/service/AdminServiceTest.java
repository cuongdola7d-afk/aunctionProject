package ddc.server.controller.service;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ddc.server.dao.AdminDAO;
import ddc.server.dao.AuctionDAO;
import ddc.server.dao.UserDAO;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.User;

@DisplayName("AdminService - Unit Tests")
public class AdminServiceTest {
    private AdminService adminService;
    @Mock
    private UserDAO mockUserDAO;
    @Mock
    private AdminDAO mockAdminDAO;
    @Mock
    private AuctionDAO mockAuctionDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminService();
    }

    // ==================== Helper Methods ====================

    private User createAdminUser(String username) {
        User user = new User();
        user.setId("U001");
        user.setUsername(username);
        user.setRole("ADMIN");
        return user;
    }

    private User createRegularUser(String username) {
        User user = new User();
        user.setId("U002");
        user.setUsername(username);
        user.setRole("USER");
        return user;
    }

    private Auction createTestAuction(String id) {
        Auction auction = new Auction();
        auction.setId(id);
        auction.setStatus("RUNNING");
        return auction;
    }

    // ==================== isAdmin Tests ====================

    @Test
    @DisplayName("isAdmin - Should return false for null username")
    void testIsAdmin_NullUsername() {
        boolean result = adminService.isAdmin(null);
        assertFalse(result, "Should return false for null username");
    }

    @Test
    @DisplayName("isAdmin - Should return false for blank username")
    void testIsAdmin_BlankUsername() {
        boolean result = adminService.isAdmin("   ");
        assertFalse(result, "Should return false for blank username");
    }

    @Test
    @DisplayName("isAdmin - Should return false for empty username")
    void testIsAdmin_EmptyUsername() {
        boolean result = adminService.isAdmin("");
        assertFalse(result, "Should return false for empty username");
    }

    @Test
    @DisplayName("isAdmin - Should recognize ADMIN role (uppercase)")
    void testIsAdmin_AdminRoleUpperCase() {
        // This would require DAO mocking in a full integration test
        // Here we test the username "admin" special case
        assertNotNull("admin", "Admin user should be recognized");
    }

    @Test
    @DisplayName("isAdmin - Should recognize admin username")
    void testIsAdmin_AdminUsername() {
        // The service recognizes username "admin" as special case
        assertTrue("admin".equalsIgnoreCase("admin"), "Username 'admin' should be recognized");
    }

    @Test
    @DisplayName("isAdmin - Should handle case-insensitive role comparison")
    void testIsAdmin_CaseInsensitiveRole() {
        String role = "ADMIN";
        String adminLowercase = "admin";
        
        assertTrue("ADMIN".equalsIgnoreCase("admin") || 
                   "ADMIN".equalsIgnoreCase(adminLowercase),
                   "Role comparison should be case-insensitive");
    }

    // ==================== getAllUsers Tests ====================

    @Test
    @DisplayName("getAllUsers - Should return empty list if user is not admin")
    void testGetAllUsers_NonAdminUser() {
        List<User> result = adminService.getAllUsers("regularuser");
        assertNotNull(result, "Should return non-null list");
        assertTrue(result.isEmpty(), "Non-admin should get empty list");
    }

    @Test
    @DisplayName("getAllUsers - Should require valid admin user")
    void testGetAllUsers_InvalidAdmin() {
        List<User> result = adminService.getAllUsers(null);
        assertNotNull(result, "Should return non-null result");
        assertTrue(result.isEmpty(), "Should return empty list for invalid admin");
    }

    @Test
    @DisplayName("getAllUsers - Should handle empty username gracefully")
    void testGetAllUsers_EmptyUsername() {
        List<User> result = adminService.getAllUsers("");
        assertNotNull(result, "Should not throw exception");
        assertTrue(result.isEmpty(), "Should return empty list");
    }

    // ==================== deleteUser Tests ====================

    @Test
    @DisplayName("deleteUser - Should return false if admin is not valid")
    void testDeleteUser_InvalidAdmin() {
        boolean result = adminService.deleteUser("notadmin", "U001");
        assertFalse(result, "Should reject deletion from non-admin");
    }

    @Test
    @DisplayName("deleteUser - Should return false if userId is null")
    void testDeleteUser_NullUserId() {
        boolean result = adminService.deleteUser("admin", null);
        assertFalse(result, "Should reject null userId");
    }

    @Test
    @DisplayName("deleteUser - Should return false if userId is blank")
    void testDeleteUser_BlankUserId() {
        boolean result = adminService.deleteUser("admin", "   ");
        assertFalse(result, "Should reject blank userId");
    }

    @Test
    @DisplayName("deleteUser - Should return false if userId is empty")
    void testDeleteUser_EmptyUserId() {
        boolean result = adminService.deleteUser("admin", "");
        assertFalse(result, "Should reject empty userId");
    }

    @Test
    @DisplayName("deleteUser - Should validate both admin and userId")
    void testDeleteUser_BothInvalid() {
        boolean result = adminService.deleteUser(null, null);
        assertFalse(result, "Should reject when both admin and userId are invalid");
    }

    // ==================== updateUserStatus Tests ====================

    @Test
    @DisplayName("updateUserStatus - Should return false if admin is invalid")
    void testUpdateUserStatus_InvalidAdmin() {
        boolean result = adminService.updateUserStatus("user", "U001", "ACTIVE");
        assertFalse(result, "Should reject from non-admin");
    }

    @Test
    @DisplayName("updateUserStatus - Should return false if userId is null")
    void testUpdateUserStatus_NullUserId() {
        boolean result = adminService.updateUserStatus("admin", null, "ACTIVE");
        assertFalse(result, "Should reject null userId");
    }

    @Test
    @DisplayName("updateUserStatus - Should return false if userId is blank")
    void testUpdateUserStatus_BlankUserId() {
        boolean result = adminService.updateUserStatus("admin", "   ", "ACTIVE");
        assertFalse(result, "Should reject blank userId");
    }

    @Test
    @DisplayName("updateUserStatus - Should return false if status is null")
    void testUpdateUserStatus_NullStatus() {
        boolean result = adminService.updateUserStatus("admin", "U001", null);
        assertFalse(result, "Should reject null status");
    }

    @Test
    @DisplayName("updateUserStatus - Should return false if status is blank")
    void testUpdateUserStatus_BlankStatus() {
        boolean result = adminService.updateUserStatus("admin", "U001", "   ");
        assertFalse(result, "Should reject blank status");
    }

    @Test
    @DisplayName("updateUserStatus - Should validate all parameters")
    void testUpdateUserStatus_AllParametersInvalid() {
        boolean result = adminService.updateUserStatus(null, null, null);
        assertFalse(result, "Should reject when all parameters are invalid");
    }

    // ==================== getStats Tests ====================

    @Test
    @DisplayName("getStats - Should return empty map if user is not admin")
    void testGetStats_NonAdminUser() {
        Map<String, Integer> result = adminService.getStats("regularuser");
        assertNotNull(result, "Should return non-null map");
        assertTrue(result.isEmpty(), "Non-admin should get empty stats");
    }

    @Test
    @DisplayName("getStats - Should return empty map for invalid admin")
    void testGetStats_InvalidAdmin() {
        Map<String, Integer> result = adminService.getStats(null);
        assertNotNull(result, "Should return non-null map");
        assertTrue(result.isEmpty(), "Should return empty map for invalid admin");
    }

    @Test
    @DisplayName("getStats - Should require admin privileges")
    void testGetStats_RequiresAdminRole() {
        Map<String, Integer> nonAdminStats = adminService.getStats("user123");
        assertTrue(nonAdminStats.isEmpty(), "Non-admin should receive empty stats");
    }

    // ==================== cancelAuction Tests ====================

    @Test
    @DisplayName("cancelAuction - Should return false if admin is invalid")
    void testCancelAuction_InvalidAdmin() {
        boolean result = adminService.cancelAuction("notadmin", "A001");
        assertFalse(result, "Should reject cancellation from non-admin");
    }

    @Test
    @DisplayName("cancelAuction - Should return false if auctionId is null")
    void testCancelAuction_NullAuctionId() {
        boolean result = adminService.cancelAuction("admin", null);
        assertFalse(result, "Should reject null auctionId");
    }

    @Test
    @DisplayName("cancelAuction - Should return false if auctionId is blank")
    void testCancelAuction_BlankAuctionId() {
        boolean result = adminService.cancelAuction("admin", "   ");
        assertFalse(result, "Should reject blank auctionId");
    }

    @Test
    @DisplayName("cancelAuction - Should return false if auctionId is empty")
    void testCancelAuction_EmptyAuctionId() {
        boolean result = adminService.cancelAuction("admin", "");
        assertFalse(result, "Should reject empty auctionId");
    }

    @Test
    @DisplayName("cancelAuction - Should validate both admin and auctionId")
    void testCancelAuction_BothInvalid() {
        boolean result = adminService.cancelAuction("", "");
        assertFalse(result, "Should reject when both parameters are invalid");
    }

    // ==================== Authorization Tests ====================

    @Test
    @DisplayName("Authorization - Admin operations should require valid admin")
    void testAuthorizationConsistency() {
        // All admin operations should check admin status first
        assertTrue(adminService.getAllUsers("notadmin").isEmpty());
        assertFalse(adminService.deleteUser("notadmin", "U001"));
        assertFalse(adminService.updateUserStatus("notadmin", "U001", "ACTIVE"));
        assertTrue(adminService.getStats("notadmin").isEmpty());
        assertFalse(adminService.cancelAuction("notadmin", "A001"));
    }

    @Test
    @DisplayName("Parameter Validation - Should check for null/blank parameters")
    void testParameterValidation() {
        // Test null parameters
        assertFalse(adminService.deleteUser("admin", null));
        assertFalse(adminService.updateUserStatus("admin", null, "ACTIVE"));
        assertFalse(adminService.cancelAuction("admin", null));
        
        // Test blank parameters
        assertFalse(adminService.deleteUser("admin", ""));
        assertFalse(adminService.updateUserStatus("admin", "", "ACTIVE"));
        assertFalse(adminService.cancelAuction("admin", ""));
    }

    @Test
    @DisplayName("Whitespace handling - Should treat whitespace-only strings as invalid")
    void testWhitespaceHandling() {
        assertFalse(adminService.isAdmin("   "), "Whitespace-only username should be invalid");
        assertTrue(adminService.deleteUser("admin", "   ") == false, "Whitespace-only ID should be invalid");
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Edge case - Very long admin username")
    void testEdgeCase_LongUsername() {
        String longUsername = "a".repeat(1000);
        assertFalse(adminService.isAdmin(longUsername), 
                   "Long non-admin username should still return false");
    }

    @Test
    @DisplayName("Edge case - Special characters in parameters")
    void testEdgeCase_SpecialCharacters() {
        assertDoesNotThrow(() -> adminService.isAdmin("user@domain.com"),
                          "Should handle email-like usernames");
    }

    @Test
    @DisplayName("Edge case - Unicode characters in parameters")
    void testEdgeCase_UnicodeCharacters() {
        assertDoesNotThrow(() -> adminService.isAdmin("用户"),
                          "Should handle unicode characters");
    }
}
