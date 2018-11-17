package SSLPackage;

/**
 * Created by Greg on 11/13/18.
 */
public class ServerPacket implements java.io.Serializable{
    public String message;

    // Possible server responses
    public static final String REGISTRATION_SUCCESSFUL = "Registration successful";
    public static final String ROOM_RESERVED = "Room reserved!";
    public static final String RESERVE_FAILED = "Failed to reserve room. Check that the room is empty, " +
            "and that you're currently available to register.";
    public static final String NOT_LOGGED_IN = "You are not logged in!";
    public static final String ALREADY_LOGGED_IN = "You're already logged in!";
    public static final String LOGIN_SUCCESSFUL = "Login successful";
    public static final String LOGIN_FAILED = "Login failed";
    public static final String ADMIN_UNAUTHORIZED = "Unauthorized: you are not logged in as an administrator.";
    public static final String PLACE_STUDENT_SUCCESSFUL = "Successfully placed student!";
    public static final String PLACE_STUDENT_FAILED = "Failed to place student";
    public static final String REMOVE_STUDENT_SUCCESSFUL = "Successfully removed student!";
    public static final String REMOVE_STUDENT_FAILED = "Failed to remove student";
    public static final String UNKNOWN_ACTION = "Unknown action requested";

    public enum Responses{

    }

    public ServerPacket(String response){
        this.message = response;
    }
}
