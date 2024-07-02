import javax.swing.*;

public class Client {
    private static ChatService chatService = new ChatService(); // Assumes these services are properly implemented
    private static MessageService messageService = new MessageService();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomeScreenGUI welcomeScreen = new WelcomeScreenGUI(chatService, messageService,new Client());
            welcomeScreen.showGUI();
        });
    }
}
