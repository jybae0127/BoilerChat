import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile String username;

    private static ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> userCredentials = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Set<String>> blockList = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> userPrivacySettings = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<String>> userFriends = new ConcurrentHashMap<>();

    public ClientHandler(Socket socket) {
        this.socket = socket;
        loadUserCredentials();
        loadBlockList();
        loadFriendsList();
        loadPrivacySettings();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                handleCommand(line);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + username);
            closeEverything();
        }
    }

    private void handleCommand(String line) throws IOException {
        String[] tokens = line.split(" ", 4);
        System.out.println("Command received: " + Arrays.toString(tokens));  // Log received command

        if (tokens.length < 1) {
            out.println("ERROR: No command received");
            return;
        }
        String command = tokens[0];
        switch (command) {
            case "LOGIN":
                handleLogin(tokens);
                break;
            case "REGISTER":
                handleRegister(tokens);
                break;
            case "MESSAGE":
                System.out.println(Arrays.toString(tokens));
                handleMessage(tokens);
                break;
            case "GET_ACTIVE_USERS":
                System.out.println("Handling active users request for: " + this.username);
                sendActiveUsers();
                break;
            default:
                out.println("ERROR: Unknown command");
                System.out.println("ERROR fdasj");
                break;
        }
    }


    private void handleLogin(String[] tokens) {
        if (tokens.length != 3) {
            out.println("ERROR: LOGIN command requires username and password");
            return;
        }
        String name = tokens[1];
        String password = tokens[2];
        if (validateCredentialsFromFile(name, password)) {
            synchronized (this) {
                this.username = name;
                clients.put(name, out);
            }
            out.println("OK");
            broadcastActiveUsers();
        } else {
            System.out.println("ERROR Invalid credentials");

        }
    }

    private void handleMessage(String[] tokens) {
        String recipient = tokens[1];
        String sender = tokens[2];
        this.username = tokens[2];
        String message = tokens[3];
        if (canSendMessage(sender, recipient)) {
            broadcastMessage(sender, recipient, message);
        } else {
            PrintWriter senderOut = clients.get(sender);
            if (senderOut != null) {
                senderOut.println("ERROR: You do not have permission to message " + recipient);
            }
        }
    }

    private void handleRegister(String[] tokens) throws IOException {
        String name = tokens[1];
        String password = tokens[2];
        if (!userCredentials.containsKey(name)) {
            userCredentials.put(name, password);
            saveUserCredentials(name, password); // Save credentials to file
            this.username = name;
            clients.put(name, out);
            out.println("OK");
            broadcastActiveUsers();
        } else {
            out.println("ERROR Username already exists");
        }
    }
    private void saveUserCredentials(String name, String password) {
        String userEntry = String.format("USERNAME: %s\nPASSWORD: %s\nBIO:\n---userseparator---\n", name, password);
        try (FileWriter fw = new FileWriter("userDatabase.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(userEntry);
        } catch (IOException e) {
            System.out.println("Error writing to credentials file: " + e.getMessage());
        }
    }
    public static void loadUserCredentials() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("userDatabase.txt")));
            String[] users = content.split("---userseparator---");
            for (String user : users) {
                if (!user.trim().isEmpty()) {
                    String[] lines = user.split("\n");
                    String username = "";
                    String password = "";
                    for (String line : lines) {
                        if (line.startsWith("USERNAME: ")) {
                            username = line.substring("USERNAME: ".length()).trim();
                        } else if (line.startsWith("PASSWORD: ")) {
                            password = line.substring("PASSWORD: ".length()).trim();
                        }
                    }
                    if (!username.isEmpty() && !password.isEmpty()) {
                        userCredentials.put(username, password);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load user credentials: " + e.getMessage());
        }
    }


    private void sendActiveUsers() {
        String activeUsers = "ACTIVEUSERS " + String.join(",", clients.keySet());
        out.println(activeUsers);
    }

     boolean isUserBlocked(String sender, String recipient) {
        loadBlockList();
        Set<String> blockedBySender = blockList.getOrDefault(sender, new HashSet<>());
        Set<String> blockedByRecipient = blockList.getOrDefault(recipient, new HashSet<>());
        boolean blocked = blockedBySender.contains(recipient) || blockedByRecipient.contains(sender);
        System.out.println(sender + " blocked by " + recipient + ": " + blockedBySender.contains(recipient));
        System.out.println(recipient + " blocked by " + sender + ": " + blockedByRecipient.contains(sender));
        return blocked;
    }



    public void broadcastMessage(String sender, String recipient, String message) {
        PrintWriter senderOut = clients.get(sender);
        PrintWriter recipientOut = clients.get(recipient);

        // Prepare messages for sender and recipient
        String recipientMessage = sender + ": " + message;
        String senderMessage = "You to " + recipient + ": " + message;


        // Send message to recipient if they are connected
        if (recipientOut != null) {
            recipientOut.println(recipientMessage);
            logMessage(sender,recipient,message);
        } else {
            if (senderOut != null) {
                senderOut.println("ERROR: User " + recipient + " is not available.");
            }
        }

        // Echo the message to the sender's chat window to maintain the flow of the conversation
        if (senderOut != null) {
            senderOut.println(senderMessage);
        }
    }
    private void logMessage(String sender, String recipient, String message) {
        String chatFilename = "chat_logs.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatFilename, true))) {
            writer.write(String.format("%s: %s%n", sender, message));
        } catch (IOException e) {
            System.out.println("Error writing to chat log: " + e.getMessage());
        }
    }




    private void closeEverything() {
        try {
            clients.remove(this.username);
            broadcastActiveUsers();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Exception when closing resources: " + e.getMessage());
        }
    }
    static void broadcastActiveUsers() {
        String activeUsers = "ACTIVEUSERS " + String.join(",", clients.keySet());
        System.out.println("Broadcasting active users: " + activeUsers);  // Debug print
        for (PrintWriter writer : clients.values()) {
            writer.println(activeUsers);
        }
    }

    private boolean validateCredentialsFromFile(String username, String password) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("userDatabase.txt")));
            String[] users = content.split("---userseparator---");
            for (String user : users) {
                if (!user.trim().isEmpty()) {
                    String[] lines = user.split("\n");
                    String fileUsername = "";
                    String filePassword = "";
                    for (String line : lines) {
                        if (line.startsWith("USERNAME: ")) {
                            fileUsername = line.substring("USERNAME: ".length()).trim();
                        } else if (line.startsWith("PASSWORD: ")) {
                            filePassword = line.substring("PASSWORD: ".length()).trim();
                        }
                    }
                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to check credentials from file: " + e.getMessage());
            return false;
        }
        return false;
    }
    private void loadBlockList() {
        blockList.clear(); // Ensure the map is cleared properly
        System.out.println("Loading block list...");
        try (BufferedReader reader = new BufferedReader(new FileReader("blocklist.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    blockList.computeIfAbsent(parts[0], k -> ConcurrentHashMap.newKeySet()).add(parts[1]);
                    blockList.computeIfAbsent(parts[1], k -> ConcurrentHashMap.newKeySet()).add(parts[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load block list: " + e.getMessage());
        }
        System.out.println("Block list loaded: " + blockList);
    }
    private void loadPrivacySettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader("privacySettings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userPrivacySettings.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load privacy settings: " + e.getMessage());
        }
    }

    private void loadFriendsList() {
        try (BufferedReader reader = new BufferedReader(new FileReader("friends.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userFriends.computeIfAbsent(parts[0], k -> new HashSet<>()).add(parts[1]);
                    userFriends.computeIfAbsent(parts[1], k -> new HashSet<>()).add(parts[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load friends list: " + e.getMessage());
        }
    }
     boolean canSendMessage(String sender, String recipient) {
        String recipientPrivacy = userPrivacySettings.getOrDefault(recipient, "all_users");
        if ("friends_only".equals(recipientPrivacy)) {
            Set<String> friends = userFriends.getOrDefault(recipient, new HashSet<>());
            return friends.contains(sender);
        }
        return true; // "all_users" or no setting defaults to public
    }
}
