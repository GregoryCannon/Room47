package ServerPackage;

import SSLPackage.ClientPacket;
import SSLPackage.ServerPacket;
import SSLPackage.SslServer;
import SSLPackage.SslServerHandler;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static SSLPackage.ServerPacket.*;

public class Server {
    private static RedisDB redis;
    private static HashUtil hashUtil;
    private static StudentDataManager studentDataManager;
    private String authenticatedUser = null;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Server server = new Server();
        SslServerHandler handler = server::handle;
        SslServer sslServer = new SslServer(6667, handler);
    }

    public Server() throws NoSuchAlgorithmException{
        hashUtil = new HashUtil();
        redis = new RedisDB("localhost", 6379);
        studentDataManager = new StudentDataManager();
    }

    public ServerPacket handle(ClientPacket p) {
        switch (p.action){
            case REGISTER:
                try {
                    boolean regSuccess = registerUser(p.username, p.password, p.roomNumber, false);
                    boolean loginSuccess = logIn(p.username, p.password);
                    if (regSuccess && loginSuccess){
                        return new ServerPacket(REGISTRATION_SUCCESSFUL);
                    }
                    return new ServerPacket(REGISTRATION_FAILED);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return new ServerPacket(e.getMessage());
                }

            case REQUEST_ROOM:
                if (authenticatedUser != null){
                    boolean success = requestRoom(p.dormName, p.roomNumber);
                    if (success){
                        return new ServerPacket(RESERVE_SUCCESSFUL);
                    } else {
                        return new ServerPacket(RESERVE_FAILED);
                    }
                } else {
                    return new ServerPacket(NOT_LOGGED_IN);
                }

            case LOG_IN:
                if (authenticatedUser != null) {
                    return new ServerPacket(ALREADY_LOGGED_IN);
                } else {
                    try {
                        if (logIn(p.username, p.password)){
                            return new ServerPacket(LOGIN_SUCCESSFUL);
                        } else {
                            return new ServerPacket(LOGIN_FAILED);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return new ServerPacket(e.getMessage());
                    }
                }

            case LOG_OUT:
                if (authenticatedUser == null) {
                    return new ServerPacket(NOT_LOGGED_IN);
                } else {
                    authenticatedUser = null;
                    return new ServerPacket(LOGOUT_SUCCESSFUL);
                }

            case ADMIN_PLACE_STUDENT:
                if (authenticatedUser == null || !redis.isAdmin(authenticatedUser)){
                    return new ServerPacket(ADMIN_UNAUTHORIZED);
                } else {
                    boolean success = adminPlaceUserInRoom(p.username, p.dormName, p.roomNumber);
                    if (success) {
                        return new ServerPacket(PLACE_STUDENT_SUCCESSFUL);
                    } else {
                        return new ServerPacket(PLACE_STUDENT_FAILED);
                    }
                }

            case ADMIN_REMOVE_STUDENT:
                if (authenticatedUser == null || !redis.isAdmin(authenticatedUser)){
                    return new ServerPacket(ADMIN_UNAUTHORIZED);
                } else {
                    boolean success = adminRemoveUserFromRoom(p.dormName, p.roomNumber);
                    if (success) {
                        return new ServerPacket(REMOVE_STUDENT_SUCCESSFUL);
                    } else {
                        return new ServerPacket(REMOVE_STUDENT_FAILED);
                    }
                }

            case GET_INFO:
                if (authenticatedUser != null){
                    String info = getInfo(authenticatedUser);
                    if (info.equals(GET_INFO_FAILED)){
                        return new ServerPacket(GET_INFO_FAILED);
                    } else {
                        return new ServerPacket(info);
                    }
                } else {
                    return new ServerPacket(NOT_LOGGED_IN);
                }

            case GET_ROOMS:
                return new ServerPacket(redis.getOccupiedRooms(p.dormName));
        }
        return new ServerPacket(UNKNOWN_ACTION);
    }

    public boolean adminPlaceUserInRoom(String studentUsername, String dormName, String dormRoomNumber){
        adminRemoveUserFromRoom(dormName, dormRoomNumber);

        // Add the specified person
        redis.setDormName(studentUsername, dormName);
        redis.setDormRoomNumber(studentUsername, dormRoomNumber);
        return true;
    }

    public boolean adminRemoveUserFromRoom(String dormName, String dormRoomNumber){
        // Kick out the current occupant if there is one
        String currentOccupant = redis.getOccupantOfRoom(dormName, dormRoomNumber);
        if (!currentOccupant.equals("-1")){
            redis.setDormName(currentOccupant, "-1");
            redis.setDormRoomNumber(currentOccupant, "-1");
        }
        return true;
    }

    public void addAdmin(String username){
        redis.addAdmin(username);
    }

    public String getInfo(String username){
        String fullName = redis.getFullName(username);
        String dormName = redis.getDormName(username);
        String dormRoomNumber = redis.getDormRoomNumber(username);
        String regNumber = redis.getRoomDrawNumber(username);
        String regTime = redis.getRegistrationTime(username);
        String studentId = redis.getUserID(username);
        String isAdmin = redis.isAdmin(username) + "";
        if (regNumber == null) return GET_INFO_FAILED; // if they're not in the database
        return fullName + "|" + dormName + "|" + dormRoomNumber + "|" + regNumber + "|"
                + regTime + "|" + studentId + "|" + isAdmin;
    }

    public boolean logIn(String username, String password) throws UnsupportedEncodingException {
        String salt = redis.getSalt(username);
        if (salt == null) return false;

        String verificationHashPass = new String(hashUtil.hashPassword(salt, password), "UTF8");
        String redisHashedPassword = redis.getHashedPassword(username);

        if (redisHashedPassword != null && redisHashedPassword.equals(verificationHashPass)){
            authenticatedUser = username;
            return true;
        }
        return false;
    }

    public boolean registerUser(String username, String password, String studentID,
                                boolean regTimeInPast) throws UnsupportedEncodingException {
        // Check that their student ID is valid and they're not already registered
        if (!studentDataManager.isValidStudentId(studentID)) return false;
        if (redis.getUserID(username) != null) return false;

        // Calculate their registration time, salt, and hashed password
        String fullName = studentDataManager.getStudentFullName(studentID);
        String salt = "" + (int) (Math.random() * 999999);
        int regNumber = (int) (Math.random() * 1000);
        long regTimeMs = regTimeInPast ? System.currentTimeMillis() - 1 : calculateRegistrationTime(regNumber, 3000000);
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");

        // Add new user to database
        redis.createAccount(username, hashedPassword, ""+ regTimeMs, salt, fullName);
        redis.setRoomDrawNumber(username, "" + regNumber);
        redis.setFullName(username, fullName);
        return true;
    }

    private long calculateRegistrationTime(int registrationNumber, long timeDelta){
        return System.currentTimeMillis() + registrationNumber * timeDelta;
    }

    public boolean requestRoom(String room, String roomNumber){
        long currentTime = System.currentTimeMillis();
        long regTime = Long.parseLong(redis.getRegistrationTime(authenticatedUser));
        boolean validRegTime = regTime < currentTime;
        boolean roomEmpty = redis.getOccupantOfRoom(room, roomNumber).equals("-1");
        boolean notAlreadyInRoom = redis.getDormName(authenticatedUser).equals("-1")
                && redis.getDormRoomNumber(authenticatedUser).equals("-1");

        if (validRegTime && roomEmpty && notAlreadyInRoom){
            redis.setDormName(authenticatedUser, room);
            redis.setDormRoomNumber(authenticatedUser, roomNumber);
            return true;
        }
        return false;
    }
}
