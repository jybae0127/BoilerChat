import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
/**
*ChatServiceTest.java
*
*Our run local test
*
*@author lab 007, team 1
*
*@version April 1, 2024
*
*/
class ChatServiceTest {
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService();
    }

    @Test
    void testAddUser() {
        InterfaceUser user = chatService.addUser("user1", "password1");
        assertNotNull(user, "User should be added successfully");
    }

    @Test
    void testBlockUser() {
        InterfaceUser user1 = chatService.addUser("user12345", "password1");
        InterfaceUser user2 = chatService.addUser("user23456", "password2");
        chatService.blockUser("user12345", "user23456");
        
    }

    @Test
    void testUnblockUser() {
        InterfaceUser user1 = chatService.addUser("user12345", "password1");
        InterfaceUser user2 = chatService.addUser("user23456", "password2");
        chatService.blockUser("user12345", "user23456");
        chatService.unblockUser("user12345", "user23456");
        
    }

    @Test
    void testAuthenticateUser() {
        chatService.addUser("user1", "password1");
        assertTrue(chatService.authenticateUser("user1", "password1"), 
                   "Authentication should succeed with correct credentials");
        assertFalse(chatService.authenticateUser("user1", "wrongpassword"), 
                    "Authentication should fail with incorrect credentials");
    }

    @Test
    void testUserCreation() {
        System.out.println("Testing User Creation...");
        User newUser = new User("newUser", "password123");
        assert User.getUsers().contains(newUser) : "User creation test failed.";
        System.out.println("User creation test passed.");
        User.getUsers().clear();
        System.out.println("All tests completed.");
    }

    @Test
    void testChatListAndBio() {
        System.out.println("Testing Chat List and Bio...");
        User user = new User("userChatBio", "pass5");
        user.setChatList(new ArrayList<>(java.util.Arrays.asList(1, 2, 3)));
        user.setBio("This is a bio.");
        assert user.getChatList().equals(java.util.Arrays.asList(1, 2, 3)) : "Chat list test failed.";
        assert "This is a bio.".equals(user.getBio()) : "Bio test failed.";
        System.out.println("Chat list and bio tests passed.");
        User.getUsers().clear();
        System.out.println("All tests completed.");
    }
    void testuserWithName() {
        // Given: Assuming addUser is a method that adds a user to the static users list in User class
        User newUser = new User("existingUser", "password"); // Directly using User class here

        // When: Searching for a user that does exist
        User foundUser = User.userWithName("existingUser");

        // Then: The user should be found
        assertNotNull(foundUser, "User should be found for existing username");
        assertEquals("existingUser", foundUser.getUsername(), "Found user should have the correct username");

        // When: Searching for a user that does not exist
        User notFoundUser = User.userWithName("nonExistingUser");

        // Then: No user should be found
        assertNull(notFoundUser, "No user should be found for non-existing username");
    }
}
