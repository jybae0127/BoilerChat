/**
 * RequestProcessor.java
 *
 * A class that the server uses to access the database.
 *
 * @author team 1 lab section 7
 *
 * @version April 29, 2024
 */
public class RequestProcessor {

    //Accesses the database to attempt to create an account and report the result.
    public static String welcomeScreenCreateAccount(String username, String password) {
        if (username != null && !username.isEmpty() && password.length() >= 8) {
            User newUser = ChatService.addUser(username, password);
            if (newUser != null) {
                return "User added successfully. Username: " + username;
            } else {
                return "Failed to add user. Username may already exist.";
            }
        } else {
            if (username == null || username.isEmpty()) {
                return "Missing username.";
            } else {
                return "Password must be at least 8 characters.";
            }
        }
    }

    //Accesses the database to attempt to log in and report the result.
    public static String welcomeScreenLogin(String username, String password) {
        User.readUsersFromFile();
        if (ChatService.authenticateUser(username, password)) {
            return "success";
        } else {
            return "failure";
        }
    }
}
