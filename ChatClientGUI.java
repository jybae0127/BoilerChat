import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ChatClientGUI extends JFrame {
    private JList<String> userList;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private DefaultListModel<String> userListModel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private StyledDocument doc;
    public ChatClientGUI(String username, Socket socket) {
        this.username = username;
        this.socket = socket;
        initializeNetwork();
        initializeComponents();
        this.setTitle("Chatroom - Logged in as: " + username);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setVisible(true);
    }

    private void initializeNetwork() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this::run).start();
            System.out.println("Connected to server successfully");
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to connect to the server.");
            System.exit(1);
        }
    }

    private void initializeComponents() {
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userList.getSelectedValue() != null) {
                String selectedUser = userList.getSelectedValue();
                chatArea.setText(""); // Clear previous messages
                loadChatHistory(selectedUser); // Load the history
            }
        });
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this::sendMessage);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        this.setLayout(new BorderLayout());
        this.add(userScrollPane, BorderLayout.WEST);
        this.add(chatScrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage(ActionEvent e) {
        if (!userList.isSelectionEmpty()) {
            String selectedUser = userList.getSelectedValue();
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                out.println("MESSAGE " + this.username + " " + selectedUser + " " + message);
                chatArea.append(this.username + ": " + message + "\n");
                messageField.setText("");
                saveMessageToFile(this.username, selectedUser, message);
            }
        }
    }

    private void run() {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                System.out.println("Server says: " + line);
                if (line.startsWith("ACTIVEUSERS")) {
                    String[] activeUsers = line.substring("ACTIVEUSERS ".length()).split(",");
                    updateUserList(activeUsers);
                } else if (line.startsWith("MESSAGE ")) {
                    String messageContent = line.substring("MESSAGE ".length());
                    appendMessage(messageContent + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from server: " + e.getMessage());
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message);
        });
    }

    private void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            Arrays.stream(users)
                    .filter(user -> !user.equals(this.username)) // Exclude the current user from the list
                    .forEach(userListModel::addElement);
        });
    }

    private void loadChatHistory(String selectedUser) {
        String filename = getChatFileName(this.username, selectedUser);
        File file = new File(filename);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Ensure that the message is between the two users
                    if (line.contains(this.username + " to " + selectedUser) || line.contains(selectedUser + " to " + this.username)) {
                        chatArea.append(line + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading chat history: " + e.getMessage());
            }
        } else {
            System.out.println("No chat history found for: " + selectedUser);
        }
    }


    private String getChatFileName(String user1, String user2) {
        String[] users = { user1, user2 };
        Arrays.sort(users);
        return "chat_logs.txt";
    }

    private void saveMessageToFile(String sender, String recipient, String message) {
        String filename = getChatFileName(sender, recipient);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(sender + " to " + recipient + ": " + message + "\n");
        } catch (IOException e) {
            System.out.println("Error saving message to file: " + e.getMessage());
        }
    }

    public void requestActiveUsers() {
        out.println("GET_ACTIVE_USERS");
    }
}
