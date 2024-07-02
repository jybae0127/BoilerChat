/**
* InterfaceChat.java
* 
* An interface that the ChatSpace class uses. 
* 
* @author lab section 007, team 1
* 
* @version April 1, 2024
*/
public interface InterfaceChat {
    void sendMessage(InterfaceUser sender, InterfaceUser receiver, String message);

    void appendMessageToFile(String message);
}
