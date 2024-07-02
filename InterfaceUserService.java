import java.util.List;
/**
 *InterfaceUserService.java
 *
 *The interface for UserService.java.
 *
 *@author lab section 007, team 1
 *
 *@version April 1, 2024
 *
 */
public interface InterfaceUserService {
    boolean registerUser(String username, String password);
    boolean loginUser(String username, String password);
    List<String> searchUsers(String searchTerm);
    boolean addFriend(String username, String friendUsername);
    boolean removeFriend(String username, String friendUsername);
    boolean blockUser(String username, String blockUsername);
    boolean unblockUser(String username, String unblockUsername);

}

