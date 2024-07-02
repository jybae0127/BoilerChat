import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *MessageServiceTest.java
 *
 *Test cases to check MessageService.java
 *
 *@author lab section 007, team 1
 *
 *@version April 15, 2024
 *
 */

class MessageServiceTest {
    private MessageService messageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("usersFile", tempDir.resolve("users.txt").toString());
        System.setProperty("friendsFile", tempDir.resolve("friends.txt").toString());
        System.setProperty("blockListFile", tempDir.resolve("blocklist.txt").toString());
        System.setProperty("messagesFile", tempDir.resolve("messages.txt").toString());

        messageService = new MessageService();

        Files.write(tempDir.resolve("users.txt"), List.of("user1:pass1", "user2:pass2", "user3:pass3"));
        Files.write(tempDir.resolve("friends.txt"), List.of("user1:user2")); // user1 and user2 are friends
        Files.write(tempDir.resolve("blocklist.txt"), List.of("user3:user1")); // user3 has blocked user1
    }

    @Test
    void sendMessageSuccessfully() {
        assertFalse(messageService.sendMessage("user1", "user2", "Hello user2"), 
                    "Message should be sent successfully.");
    }

    @Test
    void sendMessageFailureUserDoesNotExist() {
        assertFalse(messageService.sendMessage("user1", "nonExistingUser", "Hello"), 
                    "Message should fail to send due to non-existing recipient.");
    }

    @Test
    void sendMessageFailureUserBlocked() {
        assertFalse(messageService.sendMessage("user1", "user3", "Hello user3"), 
                    "Message should fail to send due to being blocked.");
    }

    @Test
    void deleteMessageSuccessfully() throws Exception {
        String messageId = "user1:user2:2021-01-01 12:00:00:Test message";
        Files.write(tempDir.resolve("messages.txt"), List.of(messageId));

        assertFalse(messageService.deleteMessage(messageId), 
                    "Message should be deleted successfully.");
    }

    @Test
    void deleteMessageFailureDoesNotExist() {
        assertFalse(messageService.deleteMessage("nonExistingMessage"), 
                    "Deletion should fail for a non-existing message.");
    }
}
