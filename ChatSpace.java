import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ChatSpace.java
 *
 * Represents a chat space in which users can send messages.
 *
 * @author lab 7, team 1
 *
 * @version April 15, 2024
 */

class ChatSpace implements InterfaceChat {
    @Override
    public void sendMessage(InterfaceUser sender, InterfaceUser receiver, String message) {
        if (sender.hasBlocked(receiver) || receiver.hasBlocked(sender)) {
            System.out.println("Message blocked due to user restrictions.");
            return;
        }

        // Create a new Message object with the sender and the message content
        TimeStamp timeStamp = new TimeStamp();

        // Construct the message with timestamp, sender, receiver, and content
        // Example uses toStringFull for a detailed timestamp format
        String formattedMessage = timeStamp.toStringFull() + " | " +
                sender.getUsername() + " to " + receiver.getUsername() + ": " + message;

        // Print the formatted message
        System.out.println(formattedMessage);
        appendMessageToFile(formattedMessage);
    }
    public void appendMessageToFile(String message) {
        String filePath = "messages.txt"; // Define the path to the messages file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("An error occurred while writing the message to the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * ChatService.java
 *
 * Handles much of the functionality of the ChatSpace.
 *
 * @author lab 7, team 1
 *
 * @version April 15, 2024
 */
class ChatService implements InterfaceChatService {
    private static final String FILE_PATH = "messages.txt";
    private static final Object FILE_LOCK = new Object();
    private ArrayList<InterfaceUser> users = new ArrayList<>();
    private ArrayList<InterfaceUser> activeUsers = new ArrayList<>();
    public ChatService() {
        this.users = new ArrayList<>();
    }
    // Modify the sendMessageToGroup method to use ChatSpace's sendMessage for sending messages
    public void sendMessageToGroup(String senderUsername, String message, boolean toFriendsOnly) {
        InterfaceUser sender = findUserByUsername(senderUsername);
        ChatSpace chatSpace = new ChatSpace(); // Instance of ChatSpace for sending messages

        if (sender == null) {
            System.out.println("Sender not found.");
            return;
        }

        if (toFriendsOnly) {
            // Iterate through the sender's friend list and send the message
            sender.getFriendList().forEach(friend -> {
                chatSpace.sendMessage(sender, friend, message);
            });
        } else {
            // Send the message to all users except the sender
            users.stream()
                    .filter(user -> !user.getUsername().equals(senderUsername) && !sender.hasBlocked(user) 
                            && !user.hasBlocked(sender))
                    .forEach(user -> {
                        chatSpace.sendMessage(sender, user, message);
                    });
        }
    }

    public void deleteMessage(String username, String messageContent) {
        File originalFile = new File(FILE_PATH);
        File tempFile = new File(originalFile.getAbsolutePath() + ".tmp");

        synchronized (FILE_LOCK) {
            try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    // Update the identifier to use username instead of chatID
                    String identifier = "Username: " + username + ", Message: " + messageContent;
                    if (!currentLine.trim().equals(identifier)) {
                        writer.write(currentLine + System.lineSeparator());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            try {
                Files.move(tempFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING, 
                           StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                System.err.println("Failed to update the message file: " + e.getMessage());
            }
        }
    }

    public String generateChatID() {
        int length = 8; // Length of the chatID
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    public static synchronized User addUser(String username, String password) {
        System.out.println("Trying with username " + username + " and password " + password);
        // Check if user already exists
        User.readUsersFromFile();
        if (User.userWithNameExists(username)) {
            System.out.println("User already exists.");
            return null;
        }

        // Iterate over in-memory users list to ensure no duplication here as well
        for (InterfaceUser user : User.getUsers()) {
            if (user.getUsername().equals(username)) {
                System.out.println("User already exists in memory.");
                return null;
            }
        }

        // Assuming User class has been updated to include a constructor that accepts a password
        User newUser = new User(username, password);
        //users.add(newUser);
        // Assuming writeToUserDatabase is a method of ChatService, not User
        User.writeToUserDatabase(); // This should only append the new user's info to the file
        User.writeUsersToFile();
        System.out.println("User added: " + username);
        return newUser;
    }

    public boolean userExistsInFile(String username) {
        File file = new File("userDatabase.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("USERNAME: " + username)) {
                    return true; // Username found in file
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("User database file not found.");
        }
        return false; // Username not found in file
    }

    /*
    public boolean authenticateUser(String username, String password) {
        File file = new File("userDatabase.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String userLine = scanner.nextLine();
                if (userLine.startsWith("USERNAME: " + username)) {
                    // If the username matches, check the next line for the password
                    if (scanner.hasNextLine()) {
                        String passwordLine = scanner.nextLine();
                        // Assuming the password line immediately follows the username line
                        String actualPassword = passwordLine.substring(passwordLine.indexOf(": ") + 2);
                        return actualPassword.equals(password);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the user database file.");
        }
        return false; // User not found or password does not match
    }
    */

    public static boolean authenticateUser(String username, String password) {
        return User.userWithName(username) != null && User.userWithName(username).getPassword().equals(password);
    }

    public synchronized InterfaceUser loginUser(String username, String password) {
        InterfaceUser newUser = new User(username, password);
        users.add(newUser);
        return newUser;
    }


    @Override
    public void addUser(InterfaceUser user) {
        this.users.add(user);
    }
    public void loginUser(InterfaceUser user) {
        this.users.add(user);
    }

    @Override
    public String blockUser(String requesterUsername, String userToBlockUsername) {
        InterfaceUser requester = findUserByUsername(requesterUsername);
        InterfaceUser userToBlock = findUserByUsername(userToBlockUsername);

        if (requester == null || userToBlock == null) {
            System.out.println("One or both users not found.");
            return "One or both users not found.";
        }

        requester.addBlock(userToBlock);
        System.out.println(requesterUsername + " has successfully blocked " + userToBlockUsername);
        return requesterUsername + " has successfully blocked " + userToBlockUsername;
    }

    public String unblockUser(String requesterUsername, String userToUnblockUsername) {
        InterfaceUser requester = findUserByUsername(requesterUsername);
        User userToUnblock = findUserByUsername(userToUnblockUsername);

        if (requester == null || userToUnblock == null) {
            System.out.println("One or both users not found.");
            return "One or both users not found.";
        }
        requester.unblockUser(userToUnblock);
        System.out.println(requesterUsername + " has successfully unblocked " + userToUnblockUsername + "!");
        return requesterUsername + " has successfully unblocked " + userToUnblockUsername + "!";
    }

    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        for (InterfaceUser user : users) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    public List<InterfaceUser> searchUser(String searchTerm) {
        List<InterfaceUser> foundUsers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("userDatabase.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assumes the file format is 'username,password'
                String username = line.split(",", 2)[0];
                if (username.contains(searchTerm)) {
                    foundUsers.add(new User(username, "")); // Password is not used
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
        return foundUsers;
    }

    //a second, modified searchUser method
    //Excludes a given list of users from the list (useful for excluding the asking user themselves as well
    //as any other users that shouldn't appear on the list for various reasons)
    public List<User> searchUser(String searchTerm, ArrayList<User> usersToExclude) {
        List<User> foundUsers = new ArrayList<>();
        //Iterate through every user in user list
        for (User u : User.getUsers()) {
            if (u.getUsername().contains(searchTerm) && !User.userListContains(usersToExclude, u)) {
                foundUsers.add(u);
            }
        }
        //return result
        return foundUsers;
    }

    //A modified searchUser method which only searches within a given User list
    public List<User> searchUserInList(String searchTerm, ArrayList<User> userList) {
        List<User> foundUsers = new ArrayList<>();
        //Iterate through every user in user list
        for (User u : userList) {
            if (u.getUsername().contains(searchTerm)) {
                foundUsers.add(u);
            }
        }
        //return result
        return foundUsers;
    }

    @Override
    public String sendMessage(String senderUsername, String receiverUsername, String message) {
        // Find sender and receiver User objects
        InterfaceUser sender = findUserByUsername(senderUsername);
        InterfaceUser receiver = findUserByUsername(receiverUsername);

        if (sender == null || receiver == null) {
            System.out.println("One of the users does not exist.");
            return "One of the users does not exist.";
        }

        // Use the modified sendMessage in ChatSpace to handle message sending
        new ChatSpace().sendMessage(sender, receiver, message);

        return "Message sent successfully";
    }

    public User findUserByUsername(String username) {
        for (InterfaceUser user : users) {
            if (user.getUsername().equals(username)) {
                return (User) user;
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("userDatabase.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("USERNAME: ")) {
                    // Assuming password is on the next line
                    String passwordLine = reader.readLine();
                    String password = passwordLine.substring(10);
                    return new User(username, password);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the user database.");
            e.printStackTrace();
        }
        return null;
    }
    public void searchMessages(String searchTerm, String chatID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                boolean matchesChatID = chatID == null || line.contains("ChatID: " + chatID);
                boolean containsTerm = line.toLowerCase().contains(searchTerm.toLowerCase());
                if (matchesChatID && containsTerm) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the chat file: " + e.getMessage());
        }
    }
}
