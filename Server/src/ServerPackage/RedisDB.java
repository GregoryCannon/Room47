package ServerPackage;

//import io.lettuce.core.RedisClient;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.HashSet;
import java.util.Set;

public class RedisDB {
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private EncryptionManager encryptionManager;

    private static final String PASSWORD = "password";
    private static final String SALT = "salt";
    private static final String ROOM_DRAW_NUMBER = "roomDrawNumber";
    private static final String DORM_NAME = "dormName";
    private static final String DORM_ROOM_NUMBER = "dormRoomNumber";
    private static final String REGISTRATION_TIME = "registrationTime";
    private static final String FULL_NAME = "fullName";
    private static final String STUDENT_ID = "studentId";
    protected static final String USERS = "users";
    private static final String ADMIN = "admin";
    private static final String CLIENT_IDS = "clientIds";
    private static final String PACKET_COUNT = "packetCount";

    public RedisDB(String host, int port, EncryptionManager encryptionManager){
        RedisURI uri = RedisURI.create(host, port);
        client = RedisClient.create(uri);
        connection = client.connect();
        commands = connection.sync();
        this.encryptionManager = encryptionManager;
        startTrackingPacketCount("_");
    }

    /*
        Replacement DB Commands With Encryption
     */

    public long sadd(String key, String val){
        key = encryptionManager.AESEncrypt(key);
        val = encryptionManager.AESEncrypt(val);
        return commands.sadd(key, val);
    }

    private boolean hset(String key, String key1, String val){
        key = encryptionManager.AESEncrypt(key);
        key1 = encryptionManager.AESEncrypt(key1);
        val = encryptionManager.AESEncrypt(val);
        return commands.hset(key, key1, val);
    }

    private boolean sismember(String key, String val){
        key = encryptionManager.AESEncrypt(key);
        val = encryptionManager.AESEncrypt(val);
        return commands.sismember(key, val);
    }

    private Set<String> smembers(String key){
        key = encryptionManager.AESEncrypt(key);
        Set<String> encryptedMembers = commands.smembers(key);
        Set<String> plaintextMembers = new HashSet<>();
        for (String encMember : encryptedMembers){
            plaintextMembers.add(encryptionManager.AESDecrypt(encMember));
        }
        return plaintextMembers;
    }

    private String hget(String key, String key1){
        key = encryptionManager.AESEncrypt(key);
        key1 = encryptionManager.AESEncrypt(key1);
        String encryptedResponse = commands.hget(key, key1);
        if (encryptedResponse == null) return null;
        return encryptionManager.AESDecrypt(encryptedResponse);
    }

    private long del(String key){
        key = encryptionManager.AESEncrypt(key);
        return commands.del(key);
    }

    private long srem(String key, String val){
        key = encryptionManager.AESEncrypt(key);
        val = encryptionManager.AESEncrypt(val);
        return commands.srem(key, val);
    }



    /*-----------------------------------
        Functions used by the server
    -----------------------------------*/

    /*
        Set management
     */
    public void createAccount(String username, String hashedPassword, String registrationTime, String salt,
                              String fullName, String studentId){
            sadd(USERS, username);
            hset(username, PASSWORD, hashedPassword);
            hset(username, SALT, salt);
            hset(username, ROOM_DRAW_NUMBER, "-1");
        /*necessary if admin wants to select a room for a student that is not registered*/
        if(getDormName(username)==null) {
            hset(username, DORM_NAME, "-1");
        }
        if(getDormRoomNumber(username)==null) {
            hset(username, DORM_ROOM_NUMBER, "-1");
        }
            hset(username, REGISTRATION_TIME, registrationTime);
            hset(username, FULL_NAME, fullName);
            hset(username, STUDENT_ID, studentId);
    }

    public void startTrackingPacketCount(String clientId){
        sadd(CLIENT_IDS, clientId);
        hset(clientId, PACKET_COUNT, "1");
    }

    public long addAdmin(String username) { return sadd(ADMIN, username); }

    public boolean isAdmin(String username){
        return sismember(ADMIN, username);
    }

    public boolean isUser(String username){
        return sismember(USERS, username);
    }

    public Set<String> getAdmins(){
        return smembers(ADMIN);
    }

    public Set<String> getUsers(){
        return smembers(USERS);
    }

    public Set<String> getClientIds(){
        return smembers(CLIENT_IDS);
    }

    /*
        Rate limiting
     */

    public int getPacketCount(String clientId){
        if (sismember(CLIENT_IDS, clientId)){
            String response = hget(clientId, PACKET_COUNT);
            if (response == null){
                return 0;
            }
            return Integer.valueOf(response);
        }
        return 0;
    }

    public void setPacketCount(String clientId, int rateLimit){
        if (sismember(CLIENT_IDS, clientId)) {
            hset(clientId, PACKET_COUNT, "" + rateLimit);
        } else {
            startTrackingPacketCount(clientId);
        }
    }

    /*
        Room Occupancy
     */

    private boolean strEqual(String a, String b) {
        return !(a == null || b == null) && a.equals(b);
    }

    public void clearRoom(String dormName, String dormRoomNumber){
        String occupant;
        while (!(occupant = getOccupantOfRoom(dormName, dormRoomNumber)).equals("-1")){
            setDormName(occupant, "-1");
            setDormRoomNumber(occupant, "-1");
        }
    }

    public String getOccupantOfRoom(String dormName, String dormRoomNumber){
        //O(n) search for now
        Set<String> users = smembers(USERS);
        for (String user : users){
            String userDormName = hget(user, DORM_NAME);
            String userDormRoomNumber = hget(user, DORM_ROOM_NUMBER);
            if(strEqual(userDormName, dormName) && strEqual(userDormRoomNumber, dormRoomNumber)) {
                return user;
            }
        }
        return "-1";
    }

    // Returns a space-separated string of room numbers that are occupied, within a given dorm
    public Set<String> getOccupiedRooms(String dormName){
        Set<String> users = smembers(USERS);
        Set<String> occupiedRooms = new HashSet<>();

        for (String user : users){
            String userDormName = hget(user, DORM_NAME);
            String userDormRoomNumber = hget(user, DORM_ROOM_NUMBER);
            if (strEqual(userDormName, dormName) && !strEqual(userDormRoomNumber, "-1")) {
                occupiedRooms.add(userDormRoomNumber);
            }
        }
        return occupiedRooms;
    }

    /*
        Individual User Properties
     */

    public String getHashedPassword(String username){
        return hget(username, PASSWORD);
    }

    public void setRoomDrawNumber(String username, String roomDrawNumber){
        hset(username, ROOM_DRAW_NUMBER, roomDrawNumber);
    }

    public String getRoomDrawNumber(String username){
        return hget(username, ROOM_DRAW_NUMBER);
    }

    public void setDormName(String username, String dormRoom){
        hset(username, DORM_NAME, dormRoom);
    }

    public String getDormName(String username){
        return hget(username, DORM_NAME);
    }

    public void setDormRoomNumber(String username, String dormRoomNumber){
        hset(username, DORM_ROOM_NUMBER, dormRoomNumber);
    }

    public String getDormRoomNumber(String username){
        return hget(username, DORM_ROOM_NUMBER);
    }

    public String getSalt(String username){
        return hget(username, SALT);
    }

    public void setSalt(String username, String salt){
        hset(username, SALT, salt);
    }

    public String getRegistrationTime(String username){
        return hget(username, REGISTRATION_TIME);
    }

    public void setRegistrationTime(String username, String registrationTime){
        hset(username, REGISTRATION_TIME, registrationTime);
    }

    public String getFullName(String username){
        return hget(username, FULL_NAME);
    }

    public void setFullName(String username, String fullName){
        hset(username, FULL_NAME, fullName);
    }

    public String getUserID(String username){
        return hget(username, STUDENT_ID);
    }

    public void setUserID(String username, String userID){
        hset(username, STUDENT_ID, userID);
    }

    /*
        Verifying ID Uniqueness
     */
    public boolean studentIDAlreadyUsed(String studentId){
        for (String user : smembers(USERS)){
            String userStudentId = hget(user, STUDENT_ID);
            if (userStudentId != null && userStudentId.equals(studentId)){
                return true;
            }
        }
        return false;
    }

    /*
        DB Management
     */

    public void clearRedisDB(){
        for (String currentUser : getUsers()) {
            del(currentUser);
            srem(USERS, currentUser);
        }
        for (String clientId : getClientIds()) {
            del(clientId);
            srem(USERS, clientId);
        }
    }

    public void closeRedisConnection(){
        connection.close();
        client.shutdown();
    }
}
