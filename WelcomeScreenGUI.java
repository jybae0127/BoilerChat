import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class WelcomeScreenGUI extends JComponent implements Runnable {
    private ChatService chatService;
    private MessageService messageService;
    private JFrame frame;
    private JButton createButton, loginButton;
    public JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel usernameLabel, passwordLabel, statusLabel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public String loggedInUsername;  // This will hold the username after a successful login

    private Client client;

    public WelcomeScreenGUI(ChatService chatService, MessageService messageService, Client client){
        this.chatService = chatService;
        this.messageService = messageService;
        this.client = client;
        initializeConnection();
        run();
    }

    private void initializeConnection() {
        try {
            socket = new Socket("localhost", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    @Override
    public void run() {
        frame = new JFrame("Welcome Screen");
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustLayout();
            }
        });
        frame.setVisible(true);
    }

    private void initializeComponents() {
        // Username label and field
        usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(new Color(221, 202, 149));
        frame.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBackground(Color.BLACK);
        usernameField.setForeground(new Color(221, 202, 149));
        usernameField.setBorder(new LineBorder(new Color(221, 202, 149)));
        frame.add(usernameField);

        // Password label and field
        passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(new Color(221, 202, 149));
        frame.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBackground(Color.BLACK);
        passwordField.setForeground(new Color(221, 202, 149));
        passwordField.setBorder(new LineBorder(new Color(221, 202, 149)));
        frame.add(passwordField);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(221, 202, 149));
        loginButton.setForeground(Color.BLACK);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(this::performLogin);
        frame.add(loginButton);

        // Create account button
        createButton = new JButton("Create Account");
        createButton.setBackground(new Color(221, 202, 149));
        createButton.setForeground(Color.BLACK);
        createButton.setOpaque(true);
        createButton.setBorderPainted(false);
        createButton.addActionListener(this::openCreateAccountDialog);
        frame.add(createButton);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(new Color(221, 202, 149));
        frame.add(statusLabel);
    }

    private void adjustLayout() {
        int width = frame.getWidth();
        int height = frame.getHeight();
        int startY = (height - 190) / 2; // Starting position for the fields to center vertically

        // Centering components
        usernameLabel.setBounds((width - 300) / 2, startY, 80, 30);
        usernameField.setBounds((width - 300) / 2 + 80, startY, 220, 30);
        passwordLabel.setBounds((width - 300) / 2, startY + 40, 80, 30);
        passwordField.setBounds((width - 300) / 2 + 80, startY + 40, 220, 30);
        loginButton.setBounds((width - 300) / 2, startY + 80, 300, 30);
        createButton.setBounds((width - 300) / 2, startY + 120, 300, 30);
        statusLabel.setBounds((width - 300) / 2, startY + 160, 300, 30);
    }


    private void openCreateAccountDialog(ActionEvent e) {
        JDialog dialog = new JDialog(frame, "Create Account", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(frame);

        JTextField newUsernameField = new JTextField(20);
        newUsernameField.setBackground(Color.BLACK);
        newUsernameField.setForeground(new Color(221, 202, 149));
        newUsernameField.setBorder(new LineBorder(new Color(221, 202, 149)));

        JPasswordField newPasswordField = new JPasswordField(20);
        newPasswordField.setBackground(Color.BLACK);
        newPasswordField.setForeground(new Color(221, 202, 149));
        newPasswordField.setBorder(new LineBorder(new Color(221, 202, 149)));

        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(221, 202, 149));
        submitButton.setForeground(Color.BLACK);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);

        submitButton.addActionListener(ev -> {
            String username = newUsernameField.getText();
            String password = new String(newPasswordField.getPassword());
            createAccount(username, password);
            dialog.dispose(); // Close the dialog after account creation
        });

        dialog.add(new JLabel("Username:"));
        dialog.add(newUsernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(newPasswordField);
        dialog.add(submitButton);

        dialog.setVisible(true);
    }
    private void performLogin(ActionEvent e) {
        new Thread(() -> {
            try {
                out.println("LOGIN " + usernameField.getText() + " " + new String(passwordField.getPassword()));
                String response = in.readLine();
                SwingUtilities.invokeLater(() -> {
                    if ("OK".equals(response)) {
                        UserSession.getInstance().setUsername(usernameField.getText());  // Set the username in UserSession
                        frame.dispose();
                        ChatScreenGUI chatScreen = new ChatScreenGUI(UserSession.getInstance().getUsername(),
                                chatService, messageService);
                        chatScreen.setVisible(true);
                    } else {
                        try {
                            String response2 = in.readLine();
                            SwingUtilities.invokeLater(() -> {
                                if ("OK".equals(response2)) {
                                    UserSession.getInstance().setUsername(usernameField.getText());  // Set the username in UserSession
                                    frame.dispose();
                                    ChatScreenGUI chatScreen = new ChatScreenGUI(UserSession.getInstance().getUsername(),
                                            chatService, messageService);
                                    chatScreen.setVisible(true);
                                } else {
                                    statusLabel.setText("Login failed: " + response2);
                                }
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> statusLabel.setText("Error communicating with the server: " + ex.getMessage()));
                        }
                    }
                });
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Error communicating with the server: " + ex.getMessage()));
            }
        }).start();
    }



    private void requestActiveUsers() {
        out.println("GET_ACTIVE_USERS");
    }


    private void createAccount(String username, String password) {
        new Thread(() -> {
            try {
                out.println("REGISTER " + username + " " + password);
                String response = in.readLine();
                SwingUtilities.invokeLater(() -> {
                    if ("OK".equals(response)) {
                        statusLabel.setText("User added successfully. Username: " + username);
                    } else {
                        statusLabel.setText("Failed to add user. Username may already exist.");
                    }
                });
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Error communicating with the server."));
                ex.printStackTrace();
            }
        }).start();
    }



    public void showGUI() {
        setVisible(true);
    }
}
