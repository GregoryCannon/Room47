package SSLPackage;

import java.util.Set;

public class ServerPacket implements java.io.Serializable{
    public String message;
    public Set<String> occupiedRooms;
    public String tempPassword;

    private static final long serialVersionUID = 1234567L;

    // Possible server responses
    public static final String REGISTRATION_SUCCESSFUL = "Registration successful";
    public static final String REGISTRATION_FAILED_STUDENT_ID = "Registration failed! Check that your student ID " +
            "is valid and not previously used.";
    public static final String REGISTRATION_FAILED_USERNAME = "Registration failed! That username is already in use.";
    public static final String REGISTRATION_FAILED_PASSWORD = "Registration failed! That password doesn't meet the" +
            "password requirements: 1 capital letter, 1 lowercase letter, 1 number, and 1 special character.";
    public static final String REGISTRATION_FAILED_INTERNAL_SERVER_ERROR = "Registration failed! An internal server error occurred";
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
    public static final String REQUEST_TEMP_PASSWORD_SUCCESSFUL = "A temp password was sent to your email!";
    public static final String REQUEST_TEMP_PASSWORD_FAILED = "Failed to send a temp password.";
    public static final String RESET_PASSWORD_SUCCESSFUL = "Your password was reset!";
    public static final String RESET_PASSWORD_FAILED = "Failed to reset password!";
    public static final String RATE_LIMIT_REACHED = "You have reached your limit for requests to the server. Check back later.";
    public static final String UNKNOWN_ACTION = "Unknown action requested";

    public ServerPacket(String response){
        this.message = response;
    }

    public ServerPacket(Set<String> occupiedRooms){
        this.occupiedRooms = occupiedRooms;
    }

    public ServerPacket(String response, String tempPassword){
        this.message = response;
        this.tempPassword = tempPassword;
    }
}
