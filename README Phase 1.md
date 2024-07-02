Team 1 - Lab Section 007 - ReadMe

Test cases won't work in Vocareum, so to compile the program it should be pulled into a separate IDE. 
Run from Main.java to see the features of the program. 
Run test cases from ChatServiceTest.java. (our run local test)

Submitting on vocareum: Ethan

1. InterfaceMessage.java
- Functionality: Defines a basic contract for message handling in the chat service, with a method `sendMessage()` to send messages.
- Relationships: Implemented by classes that represent messages, ensuring they adhere to a common structure for sending messages.

2. InterfaceChat.java
- Functionality: Establishes the foundation for chat interactions, specifying a method to send messages between users.
- Relationships: Implemented by `ChatSpace`, guiding the structure for chat-related operations between users.

3. InterfaceChatService.java
- Functionality: Defines core services for the chat application, including adding users, blocking users, and sending messages.
- Relationships: `ChatService` implements this interface, providing an implementation for the defined chat service operations.

4. InterfaceUser.java
- Functionality: Outlines essential user-related operations, such as getting a username, managing block lists, and handling friend lists.
- Relationships: Directly implemented by `User`, dictating the structure for user interactions within the chat service.

5. Message.java
- Functionality: Represents a chat message, containing the author, content, and timestamp. It encapsulates the data and behavior of a message within the chat service.
- Testing: Verified through tests in `PossibleLocalTest`, ensuring messages are correctly created and their content and author are accurately represented.
- Relationships: Utilizes `User` for the author and `TimeStamp` for the message timestamp, integrating user and time data into chat messages.

6. PossibleLocalTest.java
- Functionality: Contains unit tests for validating the functionality of the chat service, including adding users, blocking/unblocking users, and sending messages.
- Testing: This class itself is designed for testing and includes methods to verify the correct behavior of user and chat functionalities.
- Relationships: Tests classes like `ChatService` and `User`, ensuring they work correctly together and individually.


7. Main.java (main method)
- Functionality: Serves as the entry point for the application, handling user input and commands to interact with the chat service.
- Testing: Functionality tested through manual testing, ensuring the application responds correctly to user commands.
- Relationships: Interacts with `ChatService` and indirectly with other classes like `User` and `Message` through service methods, orchestrating the overall functionality.

8. TimeStamp.java
- Functionality: Provides functionality for creating and managing timestamps for messages, offering detailed and short representations of time.
- Testing: Tested for accuracy in representing time in both detailed and short formats, ensuring reliability across time-related features.
- Relationships: Used by `Message` to timestamp messages, adding timing context to chat interactions.

9. ChatSpace.java
- Functionality: Implements `InterfaceChat`, managing message sending between users, including handling blocked users and persisting messages to a file.
- Testing: Testing focuses on message sending capabilities, ensuring blocked users cannot receive messages and that messages are correctly logged.
- Relationships: Relies on `User` for sender and receiver information and `TimeStamp` for timestamping messages, ensuring comprehensive message management.

10. User.java
- Functionality: Represents a user in the chat service, managing personal information, friend lists, block lists, and chat histories.
- Testing: Extensively tested in `PossibleLocalTest`, verifying functionalities like adding friends, blocking users, and user creation.
- **Relationships**: Implements `InterfaceUser`, interacting with `Message` for sending/receiving messages and with `ChatService` for broader service operations.

11. ChatService.java
-Functionality: Implements `InterfaceChatService`, providing comprehensive chat services like user management, message sending, and user search.
- Testing: Functionality tested through unit tests in `PossibleLocalTest`, ensuring reliable user and message management.
- Relationships: Utilizes `User` for handling individual user data and actions, and `Message` for managing chat messages, serving as the central hub for chat service operations.


Text Files Explanation
userDatabase.txt: where data about Users is stored. 
sampleChatFile.txt: where data about a chat space is stored (all the the messages as well as their authors and timestamps) 

Test Run:
Welcome to the Chat Room
Available commands:
  addUser <username>
  login <username> <password>
> adduser hellohello
Enter password for hellohello: a
User added: hellohello
User added successfully. Username: hellohello
> login hellohello a
Login successful. Welcome, hellohello!
Commands available after logging in:
  blockUser <requesterUsername> <userToBlockUsername>
  unblockUser <requesterUsername> <userToUnblockUsername>
  sendMessage <senderUsername> <receiverUsername> <message>
  searchmessages <searchTerm>
  userSearch <username>
  userlist
  exit
> adduser elephant
Enter password for elephant: a
User added: elephant
User added successfully. Username: elephant
> login elephant a
Login successful. Welcome, elephant!
Commands available after logging in:
  blockUser <requesterUsername> <userToBlockUsername>
  unblockUser <requesterUsername> <userToUnblockUsername>
  sendMessage <senderUsername> <receiverUsername> <message>
  searchmessages <searchTerm>
  userSearch <username>
  userlist
  exit
> blockuser elephant hellohello
elephant has successfully blocked hellohello
> unblockuser elephant hellohello
elephant has successfully unblocked hellohello!
> sendmessage elephant hellohello hey
Monday, April 1, 2024, 17:37:47 | elephant to hellohello: hey
> sendmessage hellohello elephant hello whats up
Monday, April 1, 2024, 17:38:07 | hellohello to elephant: hello whats up
> sendmessage elephant hellhello up
Monday, April 1, 2024, 17:38:19 | elephant to hellhello: up
> searchmessages up
Sunday, March 31, 2024, 21:25:15 | asdfasdf to zxcvbnmm: whats up
Monday, April 1, 2024, 17:38:07 | hellohello to elephant: hello whats up
Monday, April 1, 2024, 17:38:19 | elephant to hellhello: up
> usersearch ele
Users found:
- USERNAME: elephant
Monday, April 1, 2024, 17:37:47 | elephant to hellohello: hey
Monday, April 1, 2024, 17:38:07 | hellohello to elephant: hello whats up
Monday, April 1, 2024, 17:38:19 | elephant to hellhello: up
> userlist
Users:
- hellohello
- elephant
> exit
Exiting chat room...
