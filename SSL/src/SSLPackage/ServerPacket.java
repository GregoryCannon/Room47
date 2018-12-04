package SSLPackage;

public class ServerPacket implements java.io.Serializable{
    public String message;

    private static final long serialVersionUID = 1234567L;

    // Possible server responses
    public static final String REGISTRATION_SUCCESSFUL = "Registration successful";
    public static final String REGISTRATION_FAILED = "Registration failed! Check that your student ID is valid.";
    public static final String RESERVE_SUCCESSFUL = "Room reserved!";
    public static final String RESERVE_FAILED = "Failed to reserve room. Check that the room is empty, " +
            "and that you're currently available to register.";
    public static final String NOT_LOGGED_IN = "You are not logged in!";
    public static final String ALREADY_LOGGED_IN = "You're already logged in!";
    public static final String LOGIN_SUCCESSFUL = "Login successful";
    public static final String LOGIN_FAILED = "Login failed";
    public static final String LOGOUT_SUCCESSFUL = "Logout successful";
    public static final String ADMIN_UNAUTHORIZED = "Unauthorized: you are not logged in as an administrator.";
    public static final String PLACE_STUDENT_SUCCESSFUL = "Successfully placed student!";
    public static final String PLACE_STUDENT_FAILED = "Failed to place student";
    public static final String REMOVE_STUDENT_SUCCESSFUL = "Successfully removed student!";
    public static final String REMOVE_STUDENT_FAILED = "Failed to remove student";
    public static final String GET_INFO_FAILED = "Failed to get student info. Check that the student is registered in the database.";
    public static final String RATE_LIMIT_REACHED = "You have reached your limit for requests to the server. Check back later.";
    public static final String UNKNOWN_ACTION = "Unknown action requested";

    public ServerPacket(String response){
        this.message = response;
    }
}
