import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestsDialog extends JDialog {
    private JTextField searchField;
    private JButton searchButton, addFriendButton;
    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JScrollPane listScrollPane;
    private ChatService chatService;
    private String activeUsername;  // Store the active user's username

    private Client client;

    public FriendRequestsDialog(Frame owner, String activeUsername, ChatService chatService, Client client) {
        super(owner, "Friend requests", true);
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

        addFriendButton = new JButton("Accept request");
        addFriendButton.addActionListener(this::acceptRequest);
        addFriendButton.setEnabled(false);

        add(topPanel, BorderLayout.NORTH);
        add(listScrollPane, BorderLayout.CENTER);
        add(addFriendButton, BorderLayout.SOUTH);

        showList();
    }

    //Does a search for users with username containing a search term
    private void performSearch(ActionEvent e) {
        String searchText = searchField.getText();

        //Only search within the user's friend request list
        List<User> usersFound = chatService.searchUserInList(searchText,
                User.userWithName(activeUsername).getFriendRequests());
        listModel.clear();
        usersFound.forEach(user -> listModel.addElement(user.getUsername()));
        addFriendButton.setEnabled(!usersFound.isEmpty());
    }

    //Shown at the beginning (full list without the search term)
    public void showList() {
        List<User> usersFound = User.userWithName(activeUsername).getFriendRequests();
        listModel.clear();
        usersFound.forEach(user -> listModel.addElement(user.getUsername()));
        addFriendButton.setEnabled(!usersFound.isEmpty());
    }

    //Method for accepting a friend request
    private void acceptRequest(ActionEvent e) {
        String userToAdd = userList.getSelectedValue();
        if (userToAdd != null && !userToAdd.isEmpty()) {
            //The users are added to each other's friend lists
            User.userWithName(activeUsername).addFriend(User.userWithName(userToAdd));
            User.userWithName(userToAdd).addFriend(User.userWithName(activeUsername));

            //Remove the users from each other's pending lists
            User.userWithName(activeUsername).setFriendRequests
                    (User.removeUserFromList(User.userWithName(activeUsername).getFriendRequests(),
                            User.userWithName(userToAdd)));
            User.userWithName(userToAdd).setSentRequests
                    (User.removeUserFromList(User.userWithName(activeUsername).getSentRequests(),
                            User.userWithName(activeUsername)));

            //Make sure updates are saved
            User.writeUsersToFile();

            dispose();
            getOwner().dispose();
            new ChatScreenGUI(activeUsername, chatService, new MessageService(), client).setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "No user selected.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
