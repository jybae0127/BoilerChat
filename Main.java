import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
/**
*Main.java
*
*Our main method for now, which can display the current capabilities of our program. 
*
*@author lab section 007, team 1
*
*@version April 1, 2024
*
*/
public class Main {
    private static ChatService chatService = new ChatService();
    private static InterfaceUser loggedInUser = null;
    private static FileWriter writer;
    private static final String FILE_PATH = "chats.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Chat Room");
        showAvailableCommands(false); // Initially, show limited commands

        try {
            writer = new FileWriter(FILE_PATH, true);
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;
                String[] parts = input.split(" ", 2);
                String command = parts[0].toLowerCase();

                switch (command) {
                    case "adduser":
                        if (parts.length > 1 && parts[1].length() >= 8 && parts[1].length() <=16) {
                            System.out.print("Enter password for " + parts[1] + ": ");
                            String password = scanner.nextLine(); // Read password input
                            InterfaceUser newUser = chatService.addUser(parts[1], password);
                            if (newUser != null) {
                                System.out.println("User added successfully. Username: " + parts[1]);
                            } else {
                                System.out.println("Failed to add user. Username may already exist.");
                            }
                        } else if (parts.length < 2){
                            System.out.println("Missing username.");
                        } else {
                            System.out.println("Username is too short (must be at least 8 characters)");
                        }
                        break;
                    case "login":
                        if (parts.length > 1) {
                            String[] part = input.split("\\s+", 3);
                            if ("login".equals(part[0]) && part.length > 2) {
                                String username = part[1];
                                String password = part[2];
                                boolean validLogin = chatService.authenticateUser(username,password);


                                if (validLogin) {
                                    loggedInUser = chatService.findUserByUsername(username);
                                    System.out.println("Login successful. Welcome, " 
                                                       + loggedInUser.getUsername() + "!");
                                    showAvailableCommands(true);
                                    InterfaceUser newUser = chatService.loginUser(part[1], part[2]);
                                    if(newUser == null){
                                        System.out.println("Login failed. Please check your username and password.");
                                    }
                                } else {
                                    System.out.println("Login failed. Please check your username and password.");
                                }
                            } else {
                                System.out.println("Invalid login command. Please follow the format:" 
                                                   + "login <username> <password>");
                            }
                        }
                        break;

                    // The following commands require the user to be logged in
                    case "sendmessage":
                        if (parts.length > 1) {
                            String[] messageParts = parts[1].split(" ", 3);
                            if (messageParts.length == 3) {
                                String chatID = chatService.generateChatID();
                                chatService.sendMessage(messageParts[0], messageParts[1], messageParts[2]);
                                String formattedMessage = String.format
                                    ("ChatID: %s, From: %s, To: %s, Message: %s%n", 
                                                                        chatID, messageParts[0], messageParts[1], 
                                                                        messageParts[2]);
                                writer.write(formattedMessage); // Write to file
                            } else {
                                System.out.println("Invalid sendMessage command format.");
                            }
                        } else {
                            System.out.println("Missing parameters for sendMessage command.");
                        }
                        break;
                    case "blockuser":
                        if (parts.length > 1) {
                            String[] userParts = parts[1].split(" ");
                            if (userParts.length == 2) {
                                chatService.blockUser(userParts[0], userParts[1]);
                            } else {
                                System.out.println("Invalid blockUser command format.");
                            }
                        } else {
                            System.out.println("Missing usernames for blockUser command.");
                        }
                        break;
                    case "unblockuser":
                        if (parts.length > 1) {
                            String[] userParts = parts[1].split(" ");
                            if (userParts.length == 2) {
                                chatService.unblockUser(userParts[0], userParts[1]);
                            } else {
                                System.out.println("Invalid unblockUser command format.");
                            }
                        } else {
                            System.out.println("Missing usernames for unblockUser command.");
                        }
                        break;
                    case "usersearch":
                        String[] obj = input.split("\\s+", 2);
                        if ("usersearch".equals(obj[0]) && obj.length > 1) {
                            List<InterfaceUser> usersFound = chatService.searchUser(obj[1]);
                            if (usersFound.isEmpty()) {
                                System.out.println("No users found with the search term: " + obj[1]);
                            } else {
                                System.out.println("Users found:");
                                for (InterfaceUser user : usersFound) {
                                    System.out.println("- " + user.getUsername());
                                }
                            }
                        } else {
                            System.out.println("Invalid command. Usage: search <searchTerm>");
                        }
                    case "searchmessages":
                        if (parts.length > 1) {
                            String[] searchParts = parts[1].split(" ");
                            String searchTerm = searchParts[0];
                            String chatID = searchParts.length > 1 ? searchParts[1] : null;
                            chatService.searchMessages(searchTerm, chatID);
                        } else {
                            System.out.println("Missing search term.");
                        }
                        break;
                    case "userlist":
                        List<String> usernames = chatService.getAllUsernames();
                        if (loggedInUser == null) {
                            System.out.println("Please login to access this command.");
                            break;
                        }
                        if (usernames.isEmpty()) {
                            System.out.println("No users found.");
                        } else {
                            System.out.println("Users:");
                            for (String username : usernames) {
                                System.out.println("- " + username);
                            }
                        }
                        // Process commands that require login
                        //processCommands(command, parts);
                        break;
                    case "exit":
                        System.out.println("Exiting chat room...");
                        return;
                    default:
                        System.out.println("Unknown command.");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void showAvailableCommands(boolean isLoggedIn) {
        if (!isLoggedIn) {
            System.out.println("Available commands:");
            System.out.println("  addUser <username>");
            System.out.println("  login <username> <password>");
        } else {
            System.out.println("Commands available after logging in:");
            System.out.println("  blockUser <requesterUsername> <userToBlockUsername>");
            System.out.println("  unblockUser <requesterUsername> <userToUnblockUsername>");
            System.out.println("  sendMessage <senderUsername> <receiverUsername> <message>");
            System.out.println("  searchmessages <searchTerm>");
            System.out.println("  userSearch <username>");
            System.out.println("  userlist");
            System.out.println("  exit");
        }
    }
}
