package ServerPackage;

import SSLPackage.ClientPacket;
import SSLPackage.ServerPacket;
import SSLPackage.SslServer;
import SSLPackage.SslServerHandler;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static SSLPackage.ServerPacket.*;

/**
 * Reads and processes incoming packets, and handles all authorization checks.
 * Does not take any actions on the DB. That functionality is in ServerActor.java
 */
public class Server {
    public ServerActor actor;

    private static SslServer sslServer;
    private static String clientId;

    // This is the source of truth for these constants. All other files import them from here.
    static final String dbEncryptionKey = "CecilSagehen1987";
    static final String initVector = "encryptionIntVec";  // TODO: encrypt
    static final int RATE_LIMIT = 400;
    static final int SSL_PORT = 6667;

    private String authenticatedUser = null;


    public static void main(String[] args) throws NoSuchAlgorithmException {
        // Get DB encryption key from CLI args
        String userDbEncryptionKey = "";
        if (args.length == 1){
            userDbEncryptionKey = args[0];
        } else {
            System.out.println("Please enter the encryption key for the database, as a command line argument!");
            return;
        }

        // Initialize all dependencies
        EncryptionManager encryptionManager = new EncryptionManager(userDbEncryptionKey, initVector);
        RedisDB redis = new RedisDB("localhost", 6379, encryptionManager);
        StudentDataManager studentDataManager = new StudentDataManager(redis, encryptionManager);
        HashUtil hashUtil = new HashUtil();
        EmailManager emailManager = new EmailManager();

        // Initialize server
        Server server = new Server(redis, encryptionManager, studentDataManager, hashUtil, emailManager);
        SslServerHandler handler = server::handle;
        sslServer = new SslServer(SSL_PORT, handler);
        clientId = sslServer.getClientId();
    }

    public Server(RedisDB redis, EncryptionManager encryptionManager, StudentDataManager studentDataManager,
                  HashUtil hashUtil, EmailManager emailManager) throws NoSuchAlgorithmException{
        actor = new ServerActor(redis, studentDataManager, hashUtil, emailManager);
        clientId = "UnitTestClientId"; // Overwritten if not in a unit test
    }

    public ServerPacket handle(ClientPacket p) {
        // Rate limiting
        int packetCount = actor.getAndIncrementPacketCount(clientId);
        if (packetCount > RATE_LIMIT){
            if (sslServer != null){     // sslServer is null during unit tests
                sslServer.close();
            }
            return new ServerPacket(RATE_LIMIT_REACHED);
        }

        switch (p.action){
            case REGISTER:
                return register(p.username, p.password, p.roomNumber);
            case REQUEST_ROOM:
                return requestRoom(p.dormName, p.roomNumber);
            case LOG_IN:
                return logIn(p.username, p.password);
            case LOG_OUT:
                return logOut();
            case GET_INFO:
                return getUserInfo();
            case GET_ROOMS:
                return getOccupiedRooms(p.dormName);
            case ADMIN_PLACE_STUDENT:
                return adminPlaceStudent(p.username, p.dormName, p.roomNumber);
            case ADMIN_REMOVE_STUDENT:
                return adminRemoveStudent(p.dormName, p.roomNumber);
            case REQUEST_TEMP_PASSWORD:
                return requestTempPassword(p.username);
        }
        return new ServerPacket(UNKNOWN_ACTION);
    }

    private ServerPacket register(String username, String password, String studentID){
        // Validate the registration requirements
        String validationProblem = actor.validateRegistration(username, password, studentID);
        if (!validationProblem.equals("")){
            return new ServerPacket(validationProblem);
        }

        try {
            boolean regSuccess = actor.registerUser(username, password, studentID, false);
            boolean loginSuccess = actor.logIn(username, password);
            authenticatedUser = username;
            if (regSuccess && loginSuccess){
                return new ServerPacket(REGISTRATION_SUCCESSFUL);
            }
            return new ServerPacket(REGISTRATION_FAILED_INTERNAL_SERVER_ERROR);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ServerPacket(e.getMessage());
        }
    }

    private ServerPacket requestRoom(String dormName, String roomNumber){
        if (authenticatedUser != null){
            boolean success = actor.requestRoom(authenticatedUser, dormName, roomNumber);
            if (success){
                return new ServerPacket(RESERVE_SUCCESSFUL);
            } else {
                return new ServerPacket(RESERVE_FAILED);
            }
        } else {
            return new ServerPacket(NOT_LOGGED_IN);
        }
    }

    private ServerPacket logIn(String username, String password){
        if (authenticatedUser != null) {
            return new ServerPacket(ALREADY_LOGGED_IN);
        } else {
            try {
                if (actor.logIn(username, password)){
                    authenticatedUser = username;
                    return new ServerPacket(LOGIN_SUCCESSFUL);
                } else {
                    return new ServerPacket(LOGIN_FAILED);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return new ServerPacket(e.getMessage());
            }
        }
    }

    private ServerPacket logOut(){
        if (authenticatedUser == null) {
            return new ServerPacket(NOT_LOGGED_IN);
        } else {
            authenticatedUser = null;
            return new ServerPacket(LOGOUT_SUCCESSFUL);
        }
    }

    private ServerPacket adminPlaceStudent(String username, String dormName, String roomNumber){
        if (authenticatedUser == null || !actor.isAdmin(authenticatedUser)){
            return new ServerPacket(ADMIN_UNAUTHORIZED);
        } else {
            boolean success = actor.adminPlaceUserInRoom(username, dormName, roomNumber);
            if (success) {
                return new ServerPacket(PLACE_STUDENT_SUCCESSFUL);
            } else {
                return new ServerPacket(PLACE_STUDENT_FAILED);
            }
        }
    }

    private ServerPacket adminRemoveStudent(String dormName, String roomNumber){
        if (authenticatedUser == null || !actor.isAdmin(authenticatedUser)){
            return new ServerPacket(ADMIN_UNAUTHORIZED);
        } else {
            boolean success = actor.adminRemoveUserFromRoom(dormName, roomNumber);
            if (success) {
                return new ServerPacket(REMOVE_STUDENT_SUCCESSFUL);
            } else {
                return new ServerPacket(REMOVE_STUDENT_FAILED);
            }
        }
    }

    private ServerPacket getUserInfo(){
        if (authenticatedUser != null){
            String info = actor.getInfo(authenticatedUser);
            if (info.equals(GET_INFO_FAILED)){
                return new ServerPacket(GET_INFO_FAILED);
            } else {
                return new ServerPacket(info);
            }
        } else {
            return new ServerPacket(NOT_LOGGED_IN);
        }
    }

    private ServerPacket getOccupiedRooms(String dormName){
        return new ServerPacket(actor.getOccupiedRooms(dormName));
    }

    private ServerPacket requestTempPassword(String username){
        if (actor.requestTempPassword(username)){
            return new ServerPacket(REQUEST_TEMP_PASSWORD_SUCCESSFUL);
        } else {
            return new ServerPacket(REQUEST_TEMP_PASSWORD_FAILED);
        }
    }
}
