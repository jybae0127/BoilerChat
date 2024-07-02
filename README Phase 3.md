### Instructions for compiling and running: 

1. Pull the program into a separate IDE from Vocareum.
2. Make sure JUnit4 and JUnit 5.8.1 are added to the class path. (If you try to run the program without doing this, IntelliJ will show how to do it) 
3. Move "notification icon.png" from the folder with the classes to the most external project folder so that it can be accessed by the program.
4. Run `Server.java`.
5. With the server still running, run `Client.java`. 

### Class and interface descriptions:

User classes and interfaces: 

- `InterfaceUser.java`: The interface for the User class.
- `User.java`: A class representing a User of the social media platform. Has a username, password, friend list, block list, list of chats they have access to, bio, and a path for a profile picture, as well as a boolean indicating whether they accept messages only from friends.
- `InterfaceUserService.java`: The interface for the UserService class. 
- `UserService.java`: Handles much of the functionality for the User class. 

Messaging classes and interfaces: 

- `InterfaceChat.java`: The interface for a Chat Space. 
- `ChatSpace.java`: Represents a space in which Users can send Messages to one another.
- `InterfaceChatService.java`: The interface for ChatService. 
- `ChatService.java`: Handles a lot of the functionality for a Chat Space.
- `InterfaceMessage.java`: The interface for a Message.
- `Message.java`: Represents a message sent in a Chat Space. Has an author, contents, and a time stamp.
- `InterfaceTimeStamp.java`: The interface for the TimeStamp class. 
- `TimeStamp.java`: A class for creating the time stamps on messages. 

Client and Server classes and interfaces: 

- `ClientInterface.java`: The interface for the Client class.
- `Client.java`: The class which runs an instance of a Client which can send requests (and receive results) from the Server.
- `ClientHandlerInterface.java`: The interface for the ClientHandler class.
- `ClientHandler.java`: Handles I/O between Server and Client.  
- `ServerInterface.java`: The interface for the Server class.
- `Server.java`: The class which serves as a "main hub" for the program's processing from the server, and can handle requests from a Client.
- `RequestProcessor.java`: Used by `Server.java` in order to access the database. 

GUI: 

- `WelcomeScreenGUI.java`: The GUI for the welcome screen which handles account creation and logging in.
- `ChatScreenGUI.java`: The GUI for the "home screen" for a user which they see immediately after logging in.
- `ChatClientGUI.java`: The GUI for the chat space where a user can send messages.
- `OwnProfilePageGUI.java`: The GUI that the user sees when viewing their own profile page, where they can edit their bio or their profile picture and go to their privacy settings page. 
- `PrivacySettingsGUI.java`: The GUI that a user sees when viewing their privacy settings.
- `AddFriendDialog.java`: A dialog which can appear off of the home screen for sending a friend request.
- `RemoveFriendDialog.java`: A dialog which can appear off of the home screen for removing a friend.
- `FriendRequestsDialog.java`: A dialog which can appear off of the home screen for accepting a friend request.
- `UserBlockDialog.java`: A dialog which can appear off of the home screen for blocking a user.
- `UserUnblockDialog.java`: A dialog which can appear off of the home screen for unblocking a user. 

RunLocalTest classes: 

- `ChatServiceTest.java`: This class runs test cases for a Chat Space. Tests the chatspace-related classes. 
- `MessageServiceTest.java`: This class runs test cases for a Message. Tests the Message-related classes. 
- `UserServiceTest.java`: This class runs test cases for a User. Tests the User-related classes. 
- `ClientTest.java`: This class runs test cases for the Client. 
- `ClientHandlerTest.java`: This class runs test cases for the ClientHandler. 
- `ServerTest.java`: This class runs test cases for the Server. For the first test we produce errors to show that if a port is in use then it cannot be accessed by anything else. 

Other:

- `Main.java`: A class for showing the functionality of our program.
- `InterfaceDataStore.java`: The interface for the DataStore class. 
- `DataStore.java`: A class which handles data storage and retrieval.
