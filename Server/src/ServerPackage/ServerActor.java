package ServerPackage;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.regex.Pattern;

import static SSLPackage.ServerPacket.*;

/**
 * Created by Greg on 12/4/18.
 *
 * Handles the nitty-gritty of taking actions based on the incoming requests.
 * All interactions with the DB are managed here, NOT in Server.java
 * All packets and authorization is handled in Server.java
 */
public class ServerActor {
    private RedisDB redis;
    private HashUtil hashUtil;
    private StudentDataManager studentDataManager;

    private static final Pattern passwordFormat = Pattern.compile("(" +
            "(?=.*[a-z])" +
            "(?=.*\\d)" +
            "(?=.*[A-Z])" +
            "(?=.*[!@#$%&'()*+,-.^_`{|}~])" +
            ".{8,40})");

    // !#$%&'()*+,-./[\\\]^_`{|}~

    ServerActor(RedisDB redis, EncryptionManager encryptionManager, StudentDataManager studentDataManager,
                HashUtil hashUtil) throws NoSuchAlgorithmException {
        this.hashUtil = hashUtil;
        this.redis = redis;
        this.studentDataManager = studentDataManager;
    }

    public int getAndIncrementPacketCount(String username){
        int packetCount = redis.getPacketCount(username) + 1;
        redis.setPacketCount(username, packetCount);
        return packetCount;
    }

    /*
        Admin-related actions
     */

    public void addAdmin(String username){
        redis.addAdmin(username);
    }

    public boolean isAdmin(String username){
        return redis.isAdmin(username);
    }

    public boolean adminPlaceUserInRoom(String studentUsername, String dormName, String dormRoomNumber){
        adminRemoveUserFromRoom(dormName, dormRoomNumber);

        redis.setDormName(studentUsername, dormName);
        redis.setDormRoomNumber(studentUsername, dormRoomNumber);
        redis.sadd(redis.USERS, studentUsername);
        return true;
    }

    public boolean adminRemoveUserFromRoom(String dormName, String dormRoomNumber){
        String currentOccupant = redis.getOccupantOfRoom(dormName, dormRoomNumber);
        if (!currentOccupant.equals("-1")){
            redis.setDormName(currentOccupant, "-1");
            redis.setDormRoomNumber(currentOccupant, "-1");
        }
        return true;
    }

    /*
        Querying info
     */

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

    public Set<String> getOccupiedRooms(String dormName){
        return redis.getOccupiedRooms(dormName);
    }

    /*
        Authentication
     */

    public String validateRegistration(String username, String password, String studentID){
        if (!studentDataManager.isValidStudentId(studentID)) {
            return REGISTRATION_FAILED_STUDENT_ID;
        }
        if (redis.studentIDAlreadyUsed(studentID)) {
            return REGISTRATION_FAILED_STUDENT_ID;
        }
        if (redis.isUser(username)){
            return REGISTRATION_FAILED_USERNAME;
        }
        if (!passwordFormat.matcher(password).matches()){
            return REGISTRATION_FAILED_PASSWORD;
        }
        return "";
    }

    public boolean registerUser(String username, String password, String studentID,
                                boolean regTimeInPast) throws UnsupportedEncodingException {
        // Calculate their registration time, salt, and hashed password
        String fullName = studentDataManager.getStudentFullName(studentID);
        String salt = "" + (int) (Math.random() * 999999);
        int regNumber = (int) (Math.random() * 1000);
        long regTimeMs = regTimeInPast ? System.currentTimeMillis() - 1 : calculateRegistrationTime(regNumber, 3000000);
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");

        // Add new user to database
        redis.createAccount(username, hashedPassword, ""+ regTimeMs, salt, fullName, studentID);
        redis.setRoomDrawNumber(username, "" + regNumber);
        redis.setFullName(username, fullName);
        return true;
    }

    public boolean logIn(String username, String password) throws UnsupportedEncodingException {
        String salt = redis.getSalt(username);
        if (salt == null) return false;

        String verificationHashPass = new String(hashUtil.hashPassword(salt, password), "UTF8");
        String redisHashedPassword = redis.getHashedPassword(username);

        return (redisHashedPassword != null && redisHashedPassword.equals(verificationHashPass));
    }

    private long calculateRegistrationTime(int registrationNumber, long timeDelta){
        return System.currentTimeMillis() + registrationNumber * timeDelta;
    }

    /*
        Requesting a room
     */

    public boolean requestRoom(String username, String room, String roomNumber){
        long currentTime = System.currentTimeMillis();
        long regTime = Long.parseLong(redis.getRegistrationTime(username));
        boolean validRegTime = regTime < currentTime;
        boolean roomEmpty = redis.getOccupantOfRoom(room, roomNumber).equals("-1");
        boolean notAlreadyInRoom = redis.getDormName(username).equals("-1")
                && redis.getDormRoomNumber(username).equals("-1");

        if (validRegTime && roomEmpty && notAlreadyInRoom){
            redis.setDormName(username, room);
            redis.setDormRoomNumber(username, roomNumber);
            return true;
        }
        return false;
    }
}
