import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class UserUnblockDialog extends JDialog {
    private JTextField searchField;
    private JButton searchButton, unblockButton;
    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JScrollPane listScrollPane;
    private ChatService chatService;
    private String activeUsername;

    public UserUnblockDialog(Frame owner, String activeUsername, ChatService chatService) {
        super(owner, "Unblock User", true);
        this.activeUsername = activeUsername;
        this.chatService = chatService;
        setupGUI();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listScrollPane = new JScrollPane(userList);

        unblockButton = new JButton("Unblock Selected User");
        unblockButton.addActionListener(this::unblockUser);
        unblockButton.setEnabled(false);

        add(inputPanel, BorderLayout.NORTH);
        add(listScrollPane, BorderLayout.CENTER);
        add(unblockButton, BorderLayout.SOUTH);
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
        unblockButton.setEnabled(!usersFound.isEmpty());
    }
    
        private void unblockUser(ActionEvent e) {
        String userToUnblock = userList.getSelectedValue();
        if (userToUnblock != null && !userToUnblock.isEmpty()) {
            String response = chatService.blockUser(activeUsername, userToUnblock);
            JOptionPane.showMessageDialog(this, response, "Blocking Result", JOptionPane.INFORMATION_MESSAGE);
            if (response.contains("successfully")) {
                writeUserToUnblockList(userToUnblock);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No user selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void writeUserToUnblockList(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("unblocklist.txt", true))) {
            writer.write(username);
            writer.newLine();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to block list: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
