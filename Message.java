/**
*Message.java
*
*A class for a Message sent between Users. 
*
*@author lab section 007, team 1
*
*@version April 1, 2024
*
*/
public class Message implements InterfaceMessage {
    private User author;
    private String content;

    private TimeStamp timeStamp;

    public Message(User author, String content) {
        this.author = author;
        this.content = content;
        this.timeStamp = new TimeStamp();
    }

    public void sendMessage() {
        System.out.println(author.getUsername() + ": " + content);
    }

    // Getters
    public User getAuthor() { return author; }
    public String getContent() { return content; }
}
