import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.Socket;

public class ChatScreenGUI extends JFrame {
    private JButton logoutButton, sendMessageButton, blockUserButton, unblockUserButton, searchUserButton,
            listUsersButton, addFriendButton;
    private JTextField messageField, targetUserField;
    private JLabel messageLabel, targetUserLabel, statusLabel;
    private ChatService chatService; // Placeholder for your chat service class
    private MessageService messageService; // Placeholder for your message service class
    private JScrollPane messageScrollPane;
    private String username;

    private Client client;

    public ChatScreenGUI(String username, ChatService chatService, MessageService messageService, Client client) {
        this.username = UserSession.getInstance().getUsername();
        this.chatService = chatService;
        this.messageService = messageService;
        this.client = client;
        initializeComponents();
        setupGUI();
    }
    public void showGUI(){
        setVisible(true);
    }

    private void setupGUI() {
        setTitle("Chat Screen - Logged in as: " + this.username);
        setSize(600, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setVisible(true);
        System.out.println(this.username);

        // Adding component listener for resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustLayout();
            }
        });
    }
    private void adjustLayout() {
        int width = getWidth();
        int height = getHeight();

        // Adjusting components based on the new width and height
        sendMessageButton.setBounds(110, 90, width - 320, 30);
        blockUserButton.setBounds(110, 130, (width - 320) / 2 - 5, 30);
        unblockUserButton.setBounds(width / 2 + 5, 130, (width - 320) / 2 - 5, 30);
        searchUserButton.setBounds(110, 170, (width - 320) / 2 - 5, 30);
        listUsersButton.setBounds(width / 2 + 5, 170, (width - 320) / 2 - 5, 30);
        logoutButton.setBounds((width - 200) / 2, 210, 200, 30);
        statusLabel.setBounds(20, height - 60, width - 40, 30);
    }


    private void initializeComponents() {
        int labelWidth = 80;
        int fieldWidth = 200;

        // Buttons for actions
        sendMessageButton = initializeButton("Open Chat Room", 110, 90, fieldWidth, this::openChatRoom);
        blockUserButton = initializeButton("Block User", 110, 130, fieldWidth, this::blockUser);
        unblockUserButton = initializeButton("Unblock User", 400, 130, fieldWidth, this::unblockUser);
        searchUserButton = initializeButton("Search User", 110, 170, fieldWidth, this::searchUser);
        listUsersButton = initializeButton("List All Users", 400, 170, fieldWidth, this::listAllUsers);
        logoutButton = initializeButton("Logout", 250, 210, fieldWidth, this::logout);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(new Color(221, 202, 149));
        add(statusLabel);
    }

    private JButton initializeButton(String label, int x, int y, int width, ActionListener action) {
        JButton button = new JButton(label);
        button.setBounds(x, y, width, 30);
        button.setBackground(new Color(221, 202, 149));
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addActionListener(action);
        add(button);
        return button;
    }

    private void openChatRoom(ActionEvent e) {
        try {
            Socket socket = new Socket("localhost", 1234);
            ChatClientGUI chatGui = new ChatClientGUI(this.username, socket); // Pass the stored username
            System.out.println(this.username);
            chatGui.setVisible(true);
            chatGui.requestActiveUsers();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void blockUser(ActionEvent e) {
        String userToBlock = targetUserField.getText();
        String response = chatService.blockUser(username, userToBlock);
        statusLabel.setText(response);
    }

    private void unblockUser(ActionEvent e) {
        String userToUnblock = targetUserField.getText();
        String response = chatService.unblockUser(username, userToUnblock);
        statusLabel.setText(response);
    }

    private void searchUser(ActionEvent e) {
        // Placeholder for search functionality
    }

    private void listAllUsers(ActionEvent e) {
        // Placeholder for listing functionality
    }

    private void addFriend(ActionEvent e) {
        // Placeholder for adding friends
    }

    private void logout(ActionEvent e) {
        dispose(); // Close the window
        // Optional: Open the login window again or handle session termination
    }
}
