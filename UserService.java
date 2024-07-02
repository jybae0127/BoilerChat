import java.util.*;
import java.io.*;
/**
 *UserService.java
 *
 *Handles all user inputs like registering a user, login, adding friends, blocking and unblocking people,and
 * setting/getting message preferences.
 *
 *@author lab section 007, team 1
 *
 *@version April 15, 2024
 *
 */

public class UserService implements InterfaceUserService {
    private final String usersFile = "userDatabase.txt";
    private final String friendsFile = "friends.txt";
    private final String blockListFile = "blocklist.txt";
    private String loggedInUser = null;

    public synchronized boolean registerUser(String username, String password) {
        String theUsersFile = "userDatabase.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(theUsersFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] userDetails = currentLine.split(":");
                if (userDetails[0].equals(username)) {
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (FileWriter fw = new FileWriter(theUsersFile, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(username + ":" + password);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean loginUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] userDetails = currentLine.split(":");
                if (userDetails.length < 2) continue;

                String fileUsername = userDetails[0];
                String filePassword = userDetails[1];

                if (fileUsername.equals(username) && filePassword.equals(password)) {
                    loggedInUser = username;
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User database file not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }


    public synchronized List<String> searchUsers(String searchTerm) {
        List<String> matchingUsers = new ArrayList<>();
        String theUsersFile = "userDatabase.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(theUsersFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] userDetails = currentLine.split(":");
                if (userDetails.length < 2) continue;

                String username = userDetails[0];

                if (username.toLowerCase().contains(searchTerm.toLowerCase())) {
                    matchingUsers.add(username);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchingUsers;
    }

    public synchronized boolean addFriend(String username, String friendUsername) {
        String theFriendsFile = "friends.txt";

        List<String> currentFriendships = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(theFriendsFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains(username + ":" + friendUsername) ||
                        currentLine.contains(friendUsername + ":" + username)) {
                    return false;
                }
                currentFriendships.add(currentLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        currentFriendships.add(username + ":" + friendUsername);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(theFriendsFile))) {
            for (String friendship : currentFriendships) {
                writer.write(friendship);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public synchronized boolean removeFriend(String username, String friendUsername) {
        boolean isRemoved = false;
        List<String> updatedFriendsList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(friendsFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.equals(username + ":" + friendUsername) &&
                        !currentLine.equals(friendUsername + ":" + username)) {
                    updatedFriendsList.add(currentLine);
                } else {
                    isRemoved = true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Friends file not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (isRemoved) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(friendsFile, false))) {
                for (String friendship : updatedFriendsList) {
                    writer.write(friendship);
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public synchronized boolean blockUser(String username, String blockUsername) {
        String theBlockListFile = "blockList.txt";

        List<String> currentBlocks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(theBlockListFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.equals(username + ":" + blockUsername)) {
                    return false;
                }
                currentBlocks.add(currentLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        currentBlocks.add(username + ":" + blockUsername);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(theBlockListFile))) {
            for (String block : currentBlocks) {
                writer.write(block);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public synchronized boolean unblockUser(String username, String unblockUsername) {
        boolean isRemoved = false;
        List<String> updatedBlockList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(blockListFile))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.equals(username + ":" + unblockUsername)) {
                    updatedBlockList.add(currentLine);
                } else {
                    isRemoved = true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Block list file not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (isRemoved) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(blockListFile, false))) {
                for (String blockEntry : updatedBlockList) {
                    writer.write(blockEntry);
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
    public synchronized boolean setMessagePreference(String username, String preference) {
        List<String> updatedUsers = new ArrayList<>();
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    line = parts[0] + ":" + parts[1] + ":" + preference;
                    found = true;
                }
                updatedUsers.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!found) return false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, false))) {
            for (String user : updatedUsers) {
                writer.write(user);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized boolean getMessagePreference(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    return "allUsers".equals(parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

