import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *MessageService.java
 *
 *Handles the messaging between users. Goes through the messaging options such as friends only, all users, blocked users
 * and sends messages accordingly.
 *
 *@author lab section 007, team 1
 *
 *@version April 15, 2024
 *
 */

public class MessageService implements InterfaceMessageService {
    private final String messagesFile = "messages.txt";
    private UserService userService;
    public MessageService() {
        this.userService = new UserService();
    }

    public synchronized boolean sendMessage(String fromUsername, String toUsername, String message) {
        if (doesUserExist(fromUsername) || doesUserExist(toUsername)) {
            System.out.println("Message cannot be sent. One or both users do not exist.");
            return false;
        }

        if (isUserBlocked(fromUsername, toUsername)) {
            System.out.println("Message cannot be sent. You are blocked by the user.");
            return false;
        }

        // Check user messaging preference
        if (!userService.getMessagePreference(toUsername) && !areUsersFriends(fromUsername, toUsername)) {
            System.out.println("Message cannot be sent. User only allows messages from friends.");
            return false;
        }


        return storeMessage(fromUsername, toUsername, message);
    }

    private synchronized boolean storeMessage(String fromUsername, String toUsername, String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = now.format(formatter);
        String formattedMessage = fromUsername + ":" + toUsername + ":" + formattedTimestamp + ":" + message;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(messagesFile, true))) {
            writer.write(formattedMessage);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean areUsersFriends(String fromUsername, String toUsername) {
        String friendsFile = "friends.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(friendsFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.equals(fromUsername + ":" + toUsername) ||
                        currentLine.equals(toUsername + ":" + fromUsername)) {
                    return true; // Users are friends
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Default to not friends if an error occurs or not found
    }

    public synchronized boolean doesUserExist(String username) {
        String usersFile = "userDatabase.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] userDetails = currentLine.split(":");
                if (userDetails[0].equals(username)) {
                    return false; // User exists
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true; // Default to false if the user does not exist or an error occurs
    }


    public synchronized boolean isUserBlocked(String fromUsername, String toUsername) {
        String blockListFile = "blocklist.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(blockListFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.equals(toUsername + ":" + fromUsername)) {
                    return true; // Sender is blocked by recipient
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public synchronized boolean deleteMessage(String messageId) {
        String theMessagesFile = "messages.txt";
        List<String> remainingMessages = new ArrayList<>();
        boolean messageFound = false;

        // Read the existing messages and filter out the one to be deleted
        try (BufferedReader reader = new BufferedReader(new FileReader(theMessagesFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Use the entire line as the identifier for simplicity
                if (!currentLine.equals(messageId)) {
                    remainingMessages.add(currentLine);
                } else {
                    messageFound = true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Messages file not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Rewrite the file without the deleted message
        if (messageFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(theMessagesFile, false))) {
                for (String message : remainingMessages) {
                    writer.write(message);
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Message to delete was not found
            return false;
        }
    }
}

