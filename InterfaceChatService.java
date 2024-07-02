import java.util.List;

/**
 * InterfaceChatService.java
 *
 * The interface for ChatService.java.
 *
 *@author lab section 007, team 1
 *
 *@version April 1, 2024
 *
 */
public interface InterfaceChatService {
    void addUser(InterfaceUser user);
    String blockUser(String requesterUsername, String userToBlockUsername);

    String unblockUser(String requesterUsername, String userToBlockUsername);

    void loginUser(InterfaceUser user);
    String sendMessage(String senderUsername, String receiverUsername, String message);

    void sendMessageToGroup(String senderUsername, String message, boolean toFriendsOnly);

    void deleteMessage(String username, String messageContent);

    String generateChatID();

    //InterfaceUser addUser(String username, String password);

    boolean userExistsInFile(String username);

    //boolean authenticateUser(String username, String password);

    InterfaceUser loginUser(String username, String password);

    List<String> getAllUsernames();

    List<InterfaceUser> searchUser(String searchTerm);

    User findUserByUsername(String username);

    void searchMessages(String searchTerm, String chatID);
}
