import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageDeletionTest.java
 *
 * Test code for deleting messages
 *
 * @author section 007, team 1
 *
 * @version April 1, 2024
 */
class MessageDeletionTest {

    private File originalFile;
    private final static String FILE_PATH = "testMessages.txt"; // Example file name, adjust as needed

    // Adjust this path to match your actual temporary directory
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Setup temporary file for testing
        originalFile = new File(tempDir.toFile(), FILE_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(originalFile))) {
            writer.write("Username: testUser, Message: Hello World" + System.lineSeparator());
            writer.write("Username: anotherUser, Message: Another message" + System.lineSeparator());
        }
    }

    @Test
    void deleteMessageSuccessfully() throws IOException {
        // Assuming deleteMessage is a method of a class that needs to be instantiated
        ChatService instance = new ChatService();
        instance.deleteMessage("testUser", "Hello World");

        // Assert file does not contain the deleted message
        long count = Files.lines(originalFile.toPath())
                .filter(line -> line.contains("Username: testUser, Message: Hello World"))
                .count();

        assertFalse(count == 0, "Message should be deleted");

        // Optionally, assert the file still contains other messages
        long remainingMessages = Files.lines(originalFile.toPath())
                .filter(line -> !line.trim().isEmpty())
                .count();

        assertTrue(remainingMessages > 0, "Other messages should remain");
    }

    // Add additional test cases as needed

    @AfterEach
    void tearDown() {
        // Cleanup if required, though @TempDir should handle this
    }
}
