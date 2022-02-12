package us.logaming.myufrplanning.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUserItem {

    private final String userId;
    private final String connectionToken;
    private final String displayName;

    public LoggedInUserItem(String userId, String connectionToken, String displayName) {
        this.userId = userId;
        this.connectionToken = connectionToken;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getConnectionToken() {
        return connectionToken;
    }

    public String getDisplayName() {
        return displayName;
    }
}