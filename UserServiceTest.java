import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UserServiceTest.java
 *
 * Tests the UserService class.
 *
 * @author lab 7, team 1
 *
 * @version April 15, 2024
 */

class UserServiceTest {
    private UserService userService;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        userService = new UserService() {
            public String getUsersFilePath() {
                return tempDir.resolve("users.txt").toString();
            }

            public String getFriendsFilePath() {
                return tempDir.resolve("friends.txt").toString();
            }

            public String getBlockListFilePath() {
                return tempDir.resolve("blocklist.txt").toString();
            }
        };
    }

    @Test
    void registerUserSuccessfully() throws IOException {
        assertFalse(userService.registerUser("newUser", "password123"), "User should be registered successfully.");
    }

    @Test
    void registerUserFailureWhenAlreadyExists() throws IOException {
        userService.registerUser("userExists", "password123");
        assertFalse(userService.registerUser("userExists", "password321"), "Should not register a user twice.");
    }

    @Test
    void loginUserSuccess() throws IOException {
        userService.registerUser("userLogin", "password123");
        assertTrue(userService.loginUser("userLogin", "password123"), 
                   "User should log in successfully with correct credentials.");
    }

    @Test
    void loginUserFailure() throws IOException {
        userService.registerUser("userLoginFail", "password123");
        assertFalse(userService.loginUser("userLoginFail", "wrongPassword"), 
                    "User should not log in with incorrect credentials.");
    }

    @Test
    void searchUsersFound() throws IOException {
        userService.registerUser("searchUser1", "password");
        userService.registerUser("searchUser2", "password");
        List<String> foundUsers = userService.searchUsers("searchUser");
        assertEquals(2, foundUsers.size(), "Should find all matching users.");
    }

    @Test
    void addAndRemoveFriendSuccess() throws IOException {
        userService.registerUser("user1", "password");
        userService.registerUser("user2", "password");
        assertTrue(userService.addFriend("user1", "user2"), "Should successfully add friend.");
        assertTrue(userService.removeFriend("user1", "user2"), "Should successfully remove friend.");
    }

    @Test
    void blockAndUnblockUserSuccess() throws IOException {
        userService.registerUser("blocker", "password");
        userService.registerUser("blocked", "password");
        assertTrue(userService.blockUser("blocker", "blocked"), "Should successfully block user.");
        assertTrue(userService.unblockUser("blocker", "blocked"), "Should successfully unblock user.");
    }
}
