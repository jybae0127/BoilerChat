import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFriendDialog extends JDialog {
    private JTextField searchField;
    private JButton searchButton, addFriendButton;
    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JScrollPane listScrollPane;
    private ChatService chatService;
    private String activeUsername;  // Store the active user's username

    private Client client;

    public AddFriendDialog(Frame owner, String activeUsername, ChatService chatService, Client client) {
        super(owner, "Send friend request", true);
        this.activeUsername = activeUsername;
        this.chatService = chatService;
        this.client = client;
        setupGUI();
        setLocationRelativeTo(owner);
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

        addFriendButton = new JButton("Send request");
        addFriendButton.addActionListener(this::sendRequest);
        addFriendButton.setEnabled(false);

        add(topPanel, BorderLayout.NORTH);
        add(listScrollPane, BorderLayout.CENTER);
        add(addFriendButton, BorderLayout.SOUTH);

        showList();
    }

    //Does a search for users with username containing a search term
    private void performSearch(ActionEvent e) {
        String searchText = searchField.getText();

        // Exclude the user themselves as well as any users that are already on:
        // - their friend list
        // - their pending friend requests list
        // - their block list
        // - the list of users that are blocking them
        ArrayList<User> usersToExclude = new ArrayList<>();
        usersToExclude.addAll(User.userWithName(activeUsername).getFriendList());
        usersToExclude.addAll(User.userWithName(activeUsername).getFriendRequests());
        usersToExclude.addAll(User.userWithName(activeUsername).getSentRequests());
        usersToExclude.addAll(User.userWithName(activeUsername).getBlockList());
        usersToExclude.addAll(User.userWithName(activeUsername).getUsersThatAreBlocking());
        usersToExclude.add(User.userWithName(activeUsername));

        List<User> usersFound = chatService.searchUser(searchText, usersToExclude);
        listModel.clear();
        for (User user : usersFound) {
            String username = user.getUsername();
            if (username.startsWith("USERNAME:")) { // Check if username starts with "USERNAME:"
                listModel.addElement(username.substring(9).trim()); // Extract and add only the part after "USERNAME:"
            }
        }
        addFriendButton.setEnabled(!listModel.isEmpty());
    }


    //Shown at the beginning (full list without the search term)
    public void showList() {
        ArrayList<User> usersToExclude = new ArrayList<>();
        usersToExclude.addAll(User.userWithName(activeUsername).getFriendList());
        usersToExclude.addAll(User.userWithName(activeUsername).getFriendRequests());
        usersToExclude.addAll(User.userWithName(activeUsername).getSentRequests());
        usersToExclude.addAll(User.userWithName(activeUsername).getBlockList());
        usersToExclude.addAll(User.userWithName(activeUsername).getUsersThatAreBlocking());
        usersToExclude.add(User.userWithName(activeUsername));

        List<User> usersFound = chatService.searchUser("", usersToExclude);
        listModel.clear();
        usersFound.forEach(user -> listModel.addElement(user.getUsername()));
        addFriendButton.setEnabled(!usersFound.isEmpty());
    }

    //Sending a friend request
    private void sendRequest(ActionEvent e) {
        String userToAdd = userList.getSelectedValue();
        if (userToAdd != null && !userToAdd.isEmpty()) {

            //The Users are added to each other's pending lists
            User.userWithName(activeUsername).addSentRequest(User.userWithName(userToAdd));
            User.userWithName(userToAdd).addFriendRequest(User.userWithName(activeUsername));

        } else {
            JOptionPane.showMessageDialog(this, "No user selected.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        //Make sure updates are saved
        User.writeUsersToFile();

        dispose();
        getOwner().dispose();
        new ChatScreenGUI(activeUsername, chatService, new MessageService(), client).setVisible(true);
    }
}
