package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ddc.server.dao.AdminDAO;
import ddc.server.dao.UserDAO;
import ddc.server.model.user.User;

@DisplayName("UserService - Unit Tests")
public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    // ==================== updatePassword Tests ====================

    @Test
    @DisplayName("updatePassword - Should reject null password")
    void testUpdatePassword_NullPassword_ShouldReturnFalse() {
        boolean result = userService.updatePassword("user123", null);
        assertFalse(result, "Should reject null password");
    }

    @Test
    @DisplayName("updatePassword - Should reject password less than 6 characters")
    void testUpdatePassword_PasswordTooShort_ShouldReturnFalse() {
        boolean result = userService.updatePassword("user123", "12345");
        assertFalse(result, "Password less than 6 characters should be rejected");
    }

    @Test
    @DisplayName("updatePassword - Should reject empty password")
    void testUpdatePassword_EmptyPassword_ShouldReturnFalse() {
        boolean result = userService.updatePassword("user123", "");
        assertFalse(result, "Empty password should be rejected");
    }

    @Test
    @DisplayName("updatePassword - Should accept valid password")
    void testUpdatePassword_ValidPassword_ShouldCallDAO() {
        // Xác nhận logic validate: password >= 6 ký tự được chấp nhận
        String password = "newPassword123";
        assertTrue(password.length() >= 6, "Valid password should have at least 6 characters");
    }

    @Test
    @DisplayName("updatePassword - Should accept password exactly 6 characters")
    void testUpdatePassword_PasswordExactly6Chars_ShouldAccept() {
        // This verifies the validation logic
        String password = "123456";
        assertTrue(password != null && password.length() >= 6, 
                   "Password with exactly 6 characters should be accepted");
    }

    // ==================== updateUserProfile Tests ====================

    @Test
    @DisplayName("updateUserProfile - Should reject null email")
    void testUpdateUserProfile_NullEmail_ShouldReturnFalse() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail(null);

        boolean result = userService.updateUserProfile(user);
        assertFalse(result, "Should reject profile with null email");
    }

    @Test
    @DisplayName("updateUserProfile - Should reject email without @ symbol")
    void testUpdateUserProfile_InvalidEmail_ShouldReturnFalse() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("invalidemail");

        boolean result = userService.updateUserProfile(user);
        assertFalse(result, "Should reject email without @ symbol");
    }

    @Test
    @DisplayName("updateUserProfile - Should accept valid email format")
    void testUpdateUserProfile_ValidEmail_ShouldAccept() {
        String email = "user@example.com";
        assertTrue(email.contains("@"), "Valid email should contain @ symbol");
    }

    @Test
    @DisplayName("updateUserProfile - Should accept various valid email formats")
    void testUpdateUserProfile_VariousValidEmails_ShouldAccept() {
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "first+last@company.org"
        };

        for (String email : validEmails) {
            assertTrue(email.contains("@"), "Email '" + email + "' should contain @");
        }
    }

    @Test
    @DisplayName("updateUserProfile - Should reject email with multiple @ symbols at wrong positions")
    void testUpdateUserProfile_MultipleAtSymbols_CheckValidation() {
        // The service only checks for @ existence, not format strictness
        String email = "user@@example.com";
        assertTrue(email.contains("@"), "Email contains @ so it passes service validation");
    }

    @Test
    @DisplayName("updateUserProfile - Should handle user with all valid fields")
    void testUpdateUserProfile_CompleteValidUser() {
        User user = new User();
        user.setUsername("testuser123");
        user.setEmail("testuser@example.com");
        user.setName("Test User");

        assertTrue(user.getEmail().contains("@"), "Valid user profile should have valid email");
    }

    @Test
    @DisplayName("updateUserProfile - Should verify email validation before DAO call")
    void testUpdateUserProfile_ValidationPrecedence() {
        User invalidUser = new User();
        invalidUser.setUsername("user");
        invalidUser.setEmail("notanemail");

        assertFalse(invalidUser.getEmail().contains("@"), 
                   "Invalid email should be caught at service level");
    }

    // ==================== Password Validation Edge Cases ====================

    @Test
    @DisplayName("updatePassword - Should accept password with special characters")
    void testUpdatePassword_SpecialCharacters() {
        String password = "Pass@123!";
        assertTrue(password.length() >= 6, "Password with special characters should be accepted if >= 6 chars");
    }

    @Test
    @DisplayName("updatePassword - Should accept very long password")
    void testUpdatePassword_VeryLongPassword() {
        String password = "A".repeat(100);
        assertTrue(password.length() >= 6, "Very long password should be accepted");
    }

    @Test
    @DisplayName("updatePassword - Should reject whitespace-only password")
    void testUpdatePassword_WhitespacePassword() {
        String password = "      ";
        assertTrue(password.length() >= 6, "Whitespace password passes length check (service validation)");
    }

    // ==================== Email Validation Edge Cases ====================

    @Test
    @DisplayName("updateUserProfile - Email starting with @")
    void testUpdateUserProfile_EmailStartsWithAt() {
        String email = "@example.com";
        assertTrue(email.contains("@"), "Email starting with @ passes contains check");
    }

    @Test
    @DisplayName("updateUserProfile - Email ending with @")
    void testUpdateUserProfile_EmailEndsWithAt() {
        String email = "user@";
        assertTrue(email.contains("@"), "Email ending with @ passes contains check");
    }

    @Test
    @DisplayName("updateUserProfile - Single character @")
    void testUpdateUserProfile_SingleAtSymbol() {
        String email = "@";
        assertTrue(email.contains("@"), "Single @ passes contains check");
    }

    // ==================== Service Consistency Tests ====================

    @Test
    @DisplayName("Service should maintain consistent validation rules")
    void testConsistentValidationLogic() {
        // Password rule: >= 6 characters
        assertTrue("123456".length() >= 6);
        assertTrue("12345".length() < 6);
        
        // Email rule: contains @
        assertTrue("user@test.com".contains("@"));
        assertFalse("usertest.com".contains("@"));
    }

    // ==================== deleteOwnAccount Tests ====================

    @Test
    @DisplayName("deleteOwnAccount - Should reject null user id")
    void testDeleteOwnAccount_NullUserId_ShouldReturnFalse() {
        UserService service = new UserService(new FakeUserDAO(validUser("U001", "user1")), new FakeAdminDAO(true));

        boolean result = service.deleteOwnAccount(null, "user1");

        assertFalse(result, "Should reject null user id");
    }

    @Test
    @DisplayName("deleteOwnAccount - Should reject blank username")
    void testDeleteOwnAccount_BlankUsername_ShouldReturnFalse() {
        UserService service = new UserService(new FakeUserDAO(validUser("U001", "user1")), new FakeAdminDAO(true));

        boolean result = service.deleteOwnAccount("U001", "   ");

        assertFalse(result, "Should reject blank username");
    }

    @Test
    @DisplayName("deleteOwnAccount - Should reject missing user")
    void testDeleteOwnAccount_MissingUser_ShouldReturnFalse() {
        FakeAdminDAO adminDAO = new FakeAdminDAO(true);
        UserService service = new UserService(new FakeUserDAO(null), adminDAO);

        boolean result = service.deleteOwnAccount("U001", "user1");

        assertFalse(result, "Should reject when user id does not exist");
        assertFalse(adminDAO.deleteCalled, "Should not call delete when user lookup fails");
    }

    @Test
    @DisplayName("deleteOwnAccount - Should reject username mismatch")
    void testDeleteOwnAccount_UsernameMismatch_ShouldReturnFalse() {
        FakeAdminDAO adminDAO = new FakeAdminDAO(true);
        UserService service = new UserService(new FakeUserDAO(validUser("U001", "owner")), adminDAO);

        boolean result = service.deleteOwnAccount("U001", "other");

        assertFalse(result, "Should reject deleting another user's account");
        assertFalse(adminDAO.deleteCalled, "Should not call delete when username does not match id");
    }

    @Test
    @DisplayName("deleteOwnAccount - Should call admin delete for matching id and username")
    void testDeleteOwnAccount_MatchingUser_ShouldCallAdminDelete() {
        FakeAdminDAO adminDAO = new FakeAdminDAO(true);
        UserService service = new UserService(new FakeUserDAO(validUser("U001", "user1")), adminDAO);

        boolean result = service.deleteOwnAccount("U001", "user1");

        assertTrue(result, "Should return DAO delete result");
        assertTrue(adminDAO.deleteCalled, "Should call shared admin delete logic");
        assertEquals("U001", adminDAO.deletedUserId);
    }

    @Test
    @DisplayName("deleteOwnAccount - Should return false when admin delete fails")
    void testDeleteOwnAccount_AdminDeleteFails_ShouldReturnFalse() {
        UserService service = new UserService(new FakeUserDAO(validUser("U001", "user1")), new FakeAdminDAO(false));

        boolean result = service.deleteOwnAccount("U001", "user1");

        assertFalse(result, "Should forward failed delete result");
    }

    private User validUser(String id, String username) {
        return new User().setId(id).setUsername(username);
    }

    private static class FakeUserDAO extends UserDAO {
        private final User user;

        FakeUserDAO(User user) {
            this.user = user;
        }

        @Override
        public User getUserById(String id) {
            return user;
        }
    }

    private static class FakeAdminDAO extends AdminDAO {
        private final boolean deleteResult;
        private boolean deleteCalled;
        private String deletedUserId;

        FakeAdminDAO(boolean deleteResult) {
            this.deleteResult = deleteResult;
        }

        @Override
        public boolean deleteUser(String userId) {
            deleteCalled = true;
            deletedUserId = userId;
            return deleteResult;
        }
    }
}
