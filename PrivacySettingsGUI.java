import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PrivacySettingsGUI.java
 *
 * Creates the GUI for what a user will see when they are viewing their privacy settings.
 *
 * @author team 1, lab 007
 *
 * @version April 29, 2024
 *
 */
public class PrivacySettingsGUI extends JComponent implements Runnable {

    private JFrame frame;

    private JRadioButton friendsOnly;
    private JRadioButton allUsers;

    private JButton backButton, confirmButton;

    private User user;

    private Client client;

    public PrivacySettingsGUI(User user, Client client) {
        this.user = user;
        this.client = client;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new PrivacySettingsGUI(new User("test username", "password"),
                new Client()));
    }

    public void run() {

        //Defining the JFrame
        frame = new JFrame("privacy settings");
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //Defining the content pane
        Container content = frame.getContentPane();
        content.setBackground(new Color(221, 202, 149));

        //The "privacy settings" text at the top
        JLabel privacySettingsLabel = new JLabel("Privacy Settings");
        privacySettingsLabel.setLocation(50, 15);
        privacySettingsLabel.setSize(300, 50);
        privacySettingsLabel.setFont(new Font("", Font.PLAIN, 27));
        frame.add(privacySettingsLabel);

        //The "allow messages from" label
        JLabel allowMessagesFromLabel = new JLabel("Allow messages from: ");
        allowMessagesFromLabel.setLocation(90, 70);
        allowMessagesFromLabel.setSize(200, 20);
        frame.add(allowMessagesFromLabel);

        //Puts the two choices into a button group
        ButtonGroup allowMessagesFrom = new ButtonGroup();
        allowMessagesFrom.add(friendsOnly);
        allowMessagesFrom.add(allUsers);

        //The friends only option
        friendsOnly = new JRadioButton("friends only");
        friendsOnly.setLocation(90, 100);
        friendsOnly.setSize(100, 20);
        friendsOnly.addActionListener(actionListener);
        allowMessagesFrom.add(friendsOnly);
        frame.add(friendsOnly);

        //The all users option
        allUsers = new JRadioButton("all users");
        allUsers.setLocation(90, 120);
        allUsers.setSize(100, 20);
        allUsers.addActionListener(actionListener);
        allowMessagesFrom.add(allUsers);
        frame.add(allUsers);

        //The back button
        backButton = new JButton("back");
        backButton.setLocation(30, 220);
        backButton.setSize(70, 20);
        backButton.addActionListener(actionListener);
        frame.add(backButton);

        //The confirm button
        confirmButton = new JButton("confirm");
        confirmButton.setLocation(100, 160);
        confirmButton.setSize(80, 25);
        confirmButton.addActionListener(actionListener);
        frame.add(confirmButton);

        //Adding to the content pane
        PrivacySettingsGUI psGUI = new PrivacySettingsGUI(user, client);
        content.add(psGUI);

    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == backButton) {
                frame.dispose();
                SwingUtilities.invokeLater(new OwnProfilePageGUI(user, client));
            }

            if (e.getSource() == confirmButton) {
                if (allUsers.isSelected()) {

                } else if (friendsOnly.isSelected()) {

                }
            }

        }

    };

}
