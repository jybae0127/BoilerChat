/**
 *ClientHandlerInterface.java
 *
 *The interface for ClientHandler.java.
 *
 *@author lab section 007, team 1
 *
 *@version April 15, 2024
 *
 */
public interface ClientHandlerInterface extends Runnable {
    void run();
    void closeResources();
}
