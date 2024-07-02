import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class UserBlockDialog extends JDialog {
    private JTextField searchField;
    private JButton searchButton, blockButton;
    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JScrollPane listScrollPane;
    private ChatService chatService;
    private String activeUsername;

    public UserBlockDialog(Frame owner, String activeUsername, ChatService chatService) {
        super(owner, "Block User", true);
        this.activeUsername = activeUsername;
        this.chatService = chatService;
        setupGUI();
    }

    private void setupGUI() {
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this::performSearch);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        listScrollPane = new JScrollPane(userList);

        blockButton = new JButton("Block Selected User");
        blockButton.addActionListener(this::blockUser);
        blockButton.setEnabled(false);

        add(topPanel, BorderLayout.NORTH);
        add(listScrollPane, BorderLayout.CENTER);
        add(blockButton, BorderLayout.SOUTH);

        setLocationRelativeTo(getOwner());
    }

    private void performSearch(ActionEvent e) {
        String searchText = searchField.getText();
        List <InterfaceUser> usersFound = chatService.searchUser(searchText);
        listModel.clear(); 
        if (usersFound.isEmpty()) {
            listModel.addElement("No users found."); 
        } else {
            for (InterfaceUser user : usersFound) {
                String username = user.getUsername();
                if (username.startsWith("USERNAME:")) {
                    listModel.addElement(username.substring(9).trim()); 
                }
            }
        }
        blockButton.setEnabled(!usersFound.isEmpty()); 
    }

    private void blockUser(ActionEvent e) {
        String userToBlock = userList.getSelectedValue();
        if (userToBlock != null && !userToBlock.isEmpty()) {
            String response = chatService.blockUser(activeUsername, userToBlock);
            JOptionPane.showMessageDialog(this, response, "Blocking Result", JOptionPane.INFORMATION_MESSAGE);
            if (response.contains("successfully")) {
                writeUserToBlockList(userToBlock);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No user selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeUserToBlockList(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("blocklist.txt", true))) {
            writer.write(username);
            writer.newLine();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to block list: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
