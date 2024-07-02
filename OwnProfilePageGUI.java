import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * OwnProfilePageGUI.java
 *
 * Creates the GUI for what a user will see when they are viewing their
 * own profile page.
 *
 * @author team 1, lab 007
 *
 * @version April 29, 2024
 *
 */
public class OwnProfilePageGUI extends JComponent implements Runnable {

    private JFrame frame;

    private JLabel usernameLabel;

    private Image profilePicture;

    private JTextField bioTextField;

    private JButton changeBioButton;

    private JButton privacySettingsButton;

    private JButton backButton;

    private JButton changeProfilePictureButton;

    private OwnProfilePageGUI oppGUI;

    private User user;

    private Client client;

    public OwnProfilePageGUI(User user, Client client) {
        this.user = user;
        this.client = client;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new OwnProfilePageGUI(new User("test username", "password"),
                new Client()));
    }

    public void run() {

        frame = new JFrame("Profile page");
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Container content = frame.getContentPane();
        content.setBackground(new Color(221, 202, 149));

        //displaying the user's username at the top of the page
        usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setForeground(new Color(0, 0, 0));
        usernameLabel.setFont(new Font("serif", Font.PLAIN, 30));
        usernameLabel.setSize(200, 50);
        usernameLabel.setLocation(150, 20);
        frame.add(usernameLabel);

        //Profile picture
        ImageIcon profilePicture;
        if (user.getProfilePicture() == null) {
            //If there is no profile picture, have the button have "add profile picture" text
            changeProfilePictureButton = new JButton("<html> Add profile <br> picture");
        } else {
            //If there is a profile picture, display it
            changeProfilePictureButton =  new JButton(user.getProfilePicture());
        }
        changeProfilePictureButton.setLocation(450, 70);
        changeProfilePictureButton.setSize(150,150);
        changeProfilePictureButton.addActionListener(actionListener);
        frame.add(changeProfilePictureButton);

        //A little label indicating where the user's bio is
        JLabel bioLabel = new JLabel("Bio:");
        bioLabel.setSize(25,10);
        bioLabel.setLocation(100, 80);
        frame.add(bioLabel);

        //The text field that holds the user's bio and allows it to be updated
        bioTextField = new JTextField(user.getBio());
        bioTextField.setSize(300, 100);
        bioTextField.setLocation(100, 100);
        frame.add(bioTextField);

        //The button allowing a user to confirm an update of their bio
        changeBioButton = new JButton("update bio");
        changeBioButton.setFont(new Font("", Font.PLAIN, 12));
        changeBioButton.setSize(100, 20);
        changeBioButton.setLocation(300, 200);
        changeBioButton.addActionListener(actionListener);
        frame.add(changeBioButton);

        //The button taking the user to their privacy settings
        privacySettingsButton = new JButton("privacy settings");
        privacySettingsButton.setSize(150, 30);
        privacySettingsButton.setLocation(450, 300);
        privacySettingsButton.addActionListener(actionListener);
        frame.add(privacySettingsButton);

        //The button taking the user back to their home page
        backButton = new JButton("back");
        backButton.setSize(100, 30);
        backButton.setLocation(20, 300);
        backButton.addActionListener(actionListener);
        frame.add(backButton);

        //Adding to the content pane
        oppGUI = new OwnProfilePageGUI(user, client);
        content.add(oppGUI);

    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            //the change bio button causes the bio to update to what is in the bio text field
            if (e.getSource() == changeBioButton) {
                user.setBio(bioTextField.getText());
            }

            //the privacy settings button links to the privacy settings page
            if (e.getSource() == privacySettingsButton) {
                frame.dispose();
                SwingUtilities.invokeLater(new PrivacySettingsGUI(user, client));
            }

            //The back button links back to the home page
            if (e.getSource() == backButton) {
                frame.dispose();
                SwingUtilities.invokeLater(() -> {
                    new ChatScreenGUI(user.getUsername(), new ChatService(), new MessageService(), client);
                });
            }

            //The profile picture button allows the user to update their profile picture
            if (e.getSource() == changeProfilePictureButton) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //Define the ImageIcon
                    ImageIcon profilePictureImage = new ImageIcon(fileChooser.getSelectedFile().getPath());
                    //Resize the image
                    profilePictureImage.setImage(profilePictureImage.getImage().getScaledInstance(150,
                            150, Image.SCALE_DEFAULT));

                    user.setProfilePicture(profilePictureImage);
                    frame.dispose();
                    SwingUtilities.invokeLater(new OwnProfilePageGUI(user, client));
                }
            }

        }
    };

}
