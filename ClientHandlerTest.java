import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientHandlerTest.java
 *
 * Tests ClientHandler class
 *
 * @author lab 7, team 1
 *
 * @version April 7, 2024
 */

public class ClientHandlerTest {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private Socket serverSideSocket;
    private PrintWriter clientOut;
    private BufferedReader clientIn;

    @BeforeEach
    public void setup() throws IOException {
        serverSocket = new ServerSocket(0); // 0 means any free port
        clientSocket = new Socket("localhost", serverSocket.getLocalPort());
        serverSideSocket = serverSocket.accept(); // Accept the connection from the client

        clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
        clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Test
    public void testClientHandlerResponse() throws IOException, InterruptedException {
        // This thread will act as a server to respond to the client handler
        Thread serverThread = new Thread(() -> {
            try {
                ClientHandler handler = new ClientHandler(serverSideSocket);
                Thread handlerThread = new Thread(handler);
                handlerThread.start();
                handlerThread.join(1000); // Wait for handler to process command
            } catch (Exception e) {
                fail("Server Handler failed: " + e.getMessage());
            }
        });
        serverThread.start();

        // Send command from client side
        clientOut.println("login testuser testpass");
        // Wait to receive response
        String response = clientIn.readLine();

        assertEquals("Login successful.", response);

        serverThread.join();
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (clientOut != null) clientOut.close();
        if (clientIn != null) clientIn.close();
        if (clientSocket != null) clientSocket.close();
        if (serverSideSocket != null) serverSideSocket.close();
        if (serverSocket != null) serverSocket.close();
    }
}
