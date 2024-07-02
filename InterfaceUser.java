import java.util.ArrayList;
/**
*InterfaceUser.java
*
*The interface for User.java.
*
*@author lab section 007, team 1
*
*@version April 1, 2024
*
*/
public interface InterfaceUser {
    String getUsername();
    String getPassword();

    void addBlock(InterfaceUser user);
    void unblockUser(InterfaceUser user);
    //void unblockUser(String requesterUsername, String userToUnblockUsername);
    boolean hasBlocked(InterfaceUser user);

    ArrayList<User> getFriendList();

    void addFriend(User user);

    void removeFriend(InterfaceUser user);

    InterfaceUser findUserByUsername(String username);

    ArrayList<User> getBlockList();

    ArrayList<Integer> getChatList();

    String getProfilePicturePath();

    String getBio();

    void setUsername(String username);

    void setPassword(String password);

    void setProfilePicturePath(String path);

    void setBio(String bio);

    void setFriendList(ArrayList<User> friendList);

    void setBlockList(ArrayList<User> blockList);

    void setChatList(ArrayList<Integer> chatList);

    String toString();

}
