import javax.swing.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 *User.java
 *
 *Handles all user inputs like registering a user, login, adding friends, blocking and unblocking people,and
 * setting/getting message preferences.
 *
 *@author lab section 007, team 1
 *
 *@version April 15, 2024
 *
 */


public class User implements InterfaceUser, Serializable {
    private String username;
    private String password;
    private ArrayList<User> friendList;
    private ArrayList<User> friendsList;
    private ArrayList<User> blockList;
    private List<InterfaceUser> blockedusers = new ArrayList<>();
    private ArrayList<Integer> chatList;

    private ArrayList<User> friendRequests; //the pending friend request the user has received
    private ArrayList<User> sentRequests; //the pending friend requests a user has sent

    private ArrayList<User> usersThatAreBlocking; //the users that are blocking this User

    private String profilePicturePath;

    private ImageIcon profilePicture; // For extra credit

    private String bio;

    private boolean friendsOnlyRestriction;

    private static ArrayList<User> users = new ArrayList<>();

    /*
    A constructor meant to initalize a new User.
    */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friendList = new ArrayList<>();
        this.blockList = new ArrayList<>();
        this.chatList = new ArrayList<>();
        this.friendRequests = new ArrayList<>();
        this.sentRequests = new ArrayList<>();
        this.usersThatAreBlocking = new ArrayList<>();
        this.profilePicturePath = null; // Default to empty
        this.profilePicture = null;

        ArrayList<User> newUserList = new ArrayList<>();
        newUserList.add(this);
        users = consolidateUserLists(users, newUserList);

        readUsersFromFile();
    }

    /*
    A constructor meant to initialize a returning User who already has a friendList and blockList.
    */
    public User(String username, String password, ArrayList<User> friendList, ArrayList<User> blockList,
                ArrayList<Integer> chatList, ArrayList<User> friendRequests, ArrayList<User> sentRequests,
                ArrayList<User> usersThatAreBlocking, ImageIcon profilePicture, String bio) {
        this.username = username;
        this.password = password;
        this.friendList = friendList;
        this.blockList = blockList;
        this.chatList = chatList;
        this.friendRequests = friendRequests;
        this.sentRequests = sentRequests;
        this.usersThatAreBlocking = usersThatAreBlocking;
        this.profilePicture = profilePicture;
        this.bio = bio;

        ArrayList<User> newUserList = new ArrayList<>();
        newUserList.add(this);
        users = consolidateUserLists(users, newUserList);

        readUsersFromFile();
    }

    /*
    (For demonstration of the program)
    Generates some sample users (although they don't have any friends, messages etc when created)
     */
    public static void initializeSampleUsers() {
        User u1 = new User("giraffe123", "giraffePassword");
        User u2 = new User("owl38", "owlPassword");
        User u3 = new User("panda921", "pandaPassword");
        User u4 = new User("turtle3910", "turtlePassword");
        User u5 = new User("bear939", "bearPassword");
        User u6 = new User("kitten903", "kittenPassword");
        User u7 = new User("dragonfly42", "dragonflyPassword");
        User u8 = new User("fox502", "foxPassword");
        User u9 = new User("shark29", "sharkPassword");
        User u10 = new User("honeybee285", "honeybeePassword");

        writeUsersToFile();
    }

    public synchronized void addFriend(User user) {
        friendList.add(user);
    }
    public synchronized void removeFriend(InterfaceUser user) {
        friendList.remove(user);
    }

    public synchronized void addBlock(InterfaceUser user) {
        if (!blockedusers.contains(user)) {
            blockedusers.add(user);
        }
    }


    @Override
    public synchronized void unblockUser(InterfaceUser user) {
        this.blockedusers.remove(user);
    }

    public synchronized void unblockUser(String requesterUsername, String userToUnblockUsername) {
        InterfaceUser requester = this.findUserByUsername(requesterUsername);
        InterfaceUser userToUnblock = this.findUserByUsername(userToUnblockUsername);
        if (requester != null && userToUnblock != null) {
            requester.unblockUser((User)userToUnblock);
            System.out.println(requesterUsername + " has successfully unblocked " + userToUnblockUsername + "!");
        } else {
            System.out.println("One or both users not found.");
        }
    }
    public InterfaceUser findUserByUsername(String theUsername) {
        Iterator var2 = users.iterator();

        InterfaceUser user;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            user = (InterfaceUser)var2.next();
        } while(!user.getUsername().equals(theUsername));

        return user;
    }


    @Override
    public synchronized boolean hasBlocked(InterfaceUser user) {
        return blockedusers.contains(user);
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public ArrayList<User> getFriendList() { return friendList; }
    public ArrayList<User> getBlockList() { return blockList; }
    public ArrayList<Integer> getChatList() { return chatList; }

    public ArrayList<User> getFriendRequests() {
        return friendRequests;
    }

    public ArrayList<User> getSentRequests() {
        return sentRequests;
    }

    public ArrayList<User> getUsersThatAreBlocking() {
        return usersThatAreBlocking;
    }

    public String getProfilePicturePath() { return profilePicturePath; }
    public String getBio() {
        return bio;
    }

    public static synchronized ArrayList<User> getUsers() {
        return users;
    }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setProfilePicturePath(String path) { this.profilePicturePath = path; }

    public void setBio(String bio) {
        this.bio = bio;
        writeToUserDatabase();
    }

    public void setFriendList(ArrayList<User> friendList) {
        this.friendList = friendList;
    }

    public void setBlockList(ArrayList<User> blockList) {
        this.blockList = blockList;
    }

    public void setChatList(ArrayList<Integer> chatList) {
        this.chatList = chatList;
    }

    public void setFriendRequests(ArrayList<User> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void addFriendRequest(User user) {
        friendRequests.add(user);
    }

    public void setSentRequests(ArrayList<User> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public void addSentRequest(User user) {
        sentRequests.add(user);
    }

    public void setUsersThatAreBlocking(ArrayList<User> usersThatAreBlocking) {
        this.usersThatAreBlocking = usersThatAreBlocking;
    }

    public void addUserThatIsBlocking(User user) {
        usersThatAreBlocking.add(user);
    }

    //Returns whether a user has any pending (received) friend requests.
    public boolean hasPendingFriendRequests() {
        return !friendRequests.isEmpty();
    }

    /*
    Reads the userDatabase file and creates User objects based on that.
     */
    public static synchronized void parseUserDatabase() {

        try {

            //Defining the file reader
            BufferedReader reader = new BufferedReader(new FileReader(new File("userDatabase.txt")));

            //these variables will be used to initialize the Users from the file
            String tempUsername = null;
            String tempPassword = null;
            ArrayList<User> tempFriendList = new ArrayList<>();
            ArrayList<User> tempBlockList = new ArrayList<>();
            ArrayList<Integer> tempChatList = new ArrayList<>();
            String tempBio = "";
            boolean parsingBio = false; //to make it easier to parse the bio

            /*
            The data is going to be stored in an ArrayList of lines before it is used, because it is going to have
            to be parsed more than once.
            This is to address the challenge of parsing a friendlist or a blocklist from a file when the Users
            mentioned on those lists might not be defined until later in the file.
             */
            ArrayList<String> lines = new ArrayList<>();

            //The line that the reader is currently on
            String readingLine = reader.readLine();

            while (readingLine != null) {

                lines.add(readingLine);

                //Update the reader to the next line
                readingLine = reader.readLine();
            }

            /*
            Now the lines will be searched for just the username and password information and initialize Users
            based off of that
             */
            for (String line : lines) {

                if (line.length() >= 10 && line.substring(0, 10).equals("USERNAME: ")) {
                    tempUsername = line.substring(10);
                } else if (line.length() >= 10 && line.substring(0, 10).equals("PASSWORD: ")) {
                    tempPassword = line.substring(10);
                } else if (line.length() >= 3 && line.substring(0, 3).equals("---")) {
                    //The --- is the end marker, so initialize the User.
                    User u = new User(tempUsername, tempPassword);
                }

            }

            /*
            Now go through the lines again, filling in the friend list, block list, and chat list.
             */
            for (String line : lines) {

                if (line.length() >= 10 && line.substring(0, 10).equals("USERNAME: ")) {
                    //Going through username again so the program knows which User to update
                    tempUsername = line.substring(10);
                } else if (line.length() >= 8 && line.substring(0, 8).equals("FRIEND: ")) {
                    tempFriendList.add(userWithName(line.substring(8)));
                } else if (line.length() >= 7 && line.substring(0, 7).equals("BLOCK: ")) {
                    tempBlockList.add(userWithName(line.substring(7)));
                } else if (line.length() >= 6 && line.substring(0, 6).equals("CHAT: ")) {
                    tempChatList.add(Integer.parseInt(line.substring(6)));
                } else if (line.length() >= 4 && line.substring(0, 4).equals("BIO:")) {

                    //The bio will be on the following lines
                    parsingBio = true;

                } else if (parsingBio && !line.equals("---userseparator---")) {

                    tempBio += line + "\n";

                } else if (line.length() >= 19 && line.substring(0, 19).equals("---userseparator---")) {
                    //The ---userseparator--- is the end marker, so update the User.
                    userWithName(tempUsername).setFriendList(tempFriendList);
                    userWithName(tempUsername).setBlockList(tempBlockList);
                    userWithName(tempUsername).setChatList(tempChatList);
                    userWithName(tempUsername).setBio(tempBio);

                    //Reset the list variables for the next User
                    tempFriendList = new ArrayList<>();
                    tempBlockList = new ArrayList<>();
                    tempChatList = new ArrayList<>();
                    tempBio = "";

                    parsingBio = false;
                }

            }

            //Close the reader
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    Returns the User which has the specified username.
    */
    public static synchronized User userWithName(String name) {
        readUsersFromFile();
        for (User user : users) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        return null;
    }

    /*
    Returns whether a user with a given username exists
     */
    public static synchronized boolean userWithNameExists(String name) {
        return userWithName(name) != null;
    }

    /*
    A method which returns an ArrayList containing every User whose username contains the search term.
     */
    public static synchronized ArrayList<User> userSearch(String search) {

        ArrayList<User> result = new ArrayList<>();

        for (User u : users) {
            if (u.getUsername().contains(search)) {
                result.add(u);
            }
        }

        return result;
    }

    //Fill in this user's information based on the file
    public void readFromDatabaseForThisUser() {
        try {

            writeUsersToFile();

            BufferedReader reader = new BufferedReader(new FileReader("userDatabase.txt"));
            PrintWriter writer = new PrintWriter(new FileWriter("userDatabase.txt"));

            //These will be used to store important file line numbers (starting from 0)
            ArrayList<String> lines = new ArrayList<>();
            int usernameLine = 0;
            int userSeparatorLine = 0;

            //Iterate through all of the lines in the file to find the locations of the important lines
            String currentLine = reader.readLine();
            int counter = 0;
            boolean foundUsernameLine = false;
            while (currentLine != null) {
                lines.add(currentLine);
                if (currentLine.substring(10).equals(username)) {
                    usernameLine = counter;
                    foundUsernameLine = true;
                }
                if (foundUsernameLine && currentLine.equals("---userseparator---")) {
                    userSeparatorLine = counter;
                    break;
                }
                counter++;
                currentLine = reader.readLine();
            }

            //Set bio
            String bioString = "";
            for (int i = usernameLine + 3; i < userSeparatorLine; i++) {
                bioString += lines.get(i);
            }

            setBio(bioString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Updates the user database with the current Users
     */
    public static synchronized void writeToUserDatabase() {

        try {

            //Defining the writer
            PrintWriter writer = new PrintWriter(new FileWriter(new File("userDatabase.txt"), false));

            //Iterates through every existing user
            for (User user : users) {

                writer.write("USERNAME: " + user.getUsername() + "\n");
                writer.write("PASSWORD: " + user.getPassword() + "\n");
                //Iterate through the friend list
                if (user.getFriendList() != null) {
                    for (InterfaceUser friend : user.getFriendList()) {
                        writer.write("FRIEND: " + friend.getUsername() + "\n");
                    }
                }
                //Iterate through the block list
                if (user.getBlockList() != null) {
                    for (User block : user.getBlockList()) {
                        writer.write("BLOCK: " + block.getUsername() + "\n");
                    }
                }
                //Iterate through the chat list
                if (user.getChatList() != null) {
                    for (int chat : user.getChatList()) {
                        writer.write("CHAT: " + chat + "\n");
                    }
                }
                if (user.getBio() != null) {
                    writer.write("BIO:\n" + user.getBio() + "\n");
                } else {
                    writer.write("BIO:\n");
                }


                writer.write("---userseparator---\n");


            }

            //Closing the writer
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Writes the users (using Object I/O) to a file called "userDatabase2.txt".
    public static synchronized void writeUsersToFile() {
        try (ObjectOutputStream objectWriter =
                     new ObjectOutputStream(new FileOutputStream("userDatabase2.txt"))) {


            //Iterate through the list of users and write them to the file
            for (User u : users) {
                objectWriter.writeObject(u);
                objectWriter.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Fill the users ArrayList from the file "userDatabase2.txt" using Object I/O
    public static synchronized void readUsersFromFile() {

        try {

            //Defining object reader
            ObjectInputStream objectReader;
            try {
                objectReader = new ObjectInputStream(new FileInputStream("userDatabase2.txt"));
            } catch (EOFException | FileNotFoundException ef) {
                return;
            }

            //Prepare an ArrayList to hold the users
            ArrayList<User> usersFromFile = new ArrayList<>();

            //Move through each line in the file until there are no Users remaining (in which case an exception
            // would be thrown)
            while (true) {
                try {
                    User userToAdd = (User) objectReader.readObject();
                    usersFromFile.add(userToAdd);
                } catch (Exception e) {
                    break;
                }
            }

            //Closing reader
            objectReader.close();

            //Merging the users from the file from the users already in the users list
            users = consolidateUserLists(users, usersFromFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Updates an old user list to include the data in the new list while avoiding creating
    any duplicate entries.
     */
    public static ArrayList<User> consolidateUserLists(ArrayList<User> oldList, ArrayList<User> newList) {
        //Begin with the old list
        ArrayList<User> result = oldList;
        //Add any elements in the new list that do not already exist in the list
        for (User u: newList) {
            if (!userListContains(result, u)) {
                result.add(u);
            }
        }
        return result;
    }

    //Returns whether a User list contains a given User
    public static boolean userListContains(ArrayList<User> list, User user) {
        //Iterate through the list
        for (User u: list) {
            if (u.getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    //Returns a given User list with a given User removed
    public static ArrayList<User> removeUserFromList(ArrayList<User> list, User user) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUsername().equals(user.getUsername())) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }

    /*
    Gives String in form:
    Username: [username]
    Password: [password]
    Friend list:
    - friend
    - friend
    - friend
    Block list:
    - block
    - block
    - block
    Chat list:
    - chat
    - chat
    - chat
    Bio:
    bio
     */
    public String toString() {
        String result = "";

        result += "Username: " + username + "\n";
        result += "Password: " + password + "\n";
        result += "Friend list:\n";
        for (User u : friendList) {
            result += "- " + u.getUsername() + "\n";
        }
        result += "Block list:\n";
        for (User u : blockList) {
            result += "- " + u.getUsername() + "\n";
        }
        result += "Chat list:\n";
        for (int i : chatList) {
            result += "- " + i + "\n";
        }
        result += "Bio:\n" + bio + "\n";

        return result;
    }

    //Prints the usernames of all existing users
    public static void printUserList() {
        if (users.isEmpty()) {
            System.out.println("no users");
            return;
        }
        int counter = 1;
        for (User u : users) {
            System.out.println(counter + ". " + u.getUsername());
            counter++;
        }
    }

    public boolean getFriendsOnlyRestriction() {
        return friendsOnlyRestriction;
    }

    public void setFriendsOnlyRestriction(boolean friendsOnlyRestriction) {
        this.friendsOnlyRestriction = friendsOnlyRestriction;
    }

    /*
    Profile picture extra credit methods
     */

    public ImageIcon getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ImageIcon image) {
        profilePicture = image;
    }

    //hello
}
