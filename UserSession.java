public class UserSession {
    private static UserSession instance = new UserSession();
    private String username;

    private UserSession() {}  // Private constructor to prevent external instantiation

    public static UserSession getInstance() {
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
