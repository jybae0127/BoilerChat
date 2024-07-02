/**
 *InterfaceMessageService.java
 *
 *The interface for MessageService.java.
 *
 *@author lab section 007, team 1
 *
 *@version April 1, 2024
 *
 */
public interface InterfaceMessageService {
    boolean sendMessage(String fromUsername, String toUsername, String message);
    boolean deleteMessage(String messageId);
}
