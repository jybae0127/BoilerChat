import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class UserSearchDialog extends JDialog {
    private JTextField searchField;
    private JButton searchButton;
    private JTextArea resultArea;
    private ChatService chatService;

    public UserSearchDialog(Frame owner, ChatService chatService) {
        super(owner, "Search User", true);
        this.chatService = chatService;
        setupGUI();
    }

    private void setupGUI() {
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this::performSearch);
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(getOwner());
    }
    private void performSearch(ActionEvent e) {
        String searchText = searchField.getText();
        List <InterfaceUser> usersFound = chatService.searchUser(searchText);
        resultArea.setText("");
        if (usersFound.isEmpty()) {
            resultArea.append("No users found.");
        } else {
            resultArea.append("Users found:\n");
            for (InterfaceUser user : usersFound) {
                String username = user.getUsername();
                if (username.startsWith("USERNAME:")) {
                    resultArea.append(username.substring(9).trim() + "\n"); // Adjust index if needed
                }
            }
        }
    }
}
