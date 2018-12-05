package ServerPackage;

//import io.lettuce.core.RedisClient;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

public class RedisDB {
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private String dbEncryptionKey;

    private static final String PASSWORD = "password";
    private static final String SALT = "salt";
    private static final String ROOM_DRAW_NUMBER = "roomDrawNumber";
    private static final String DORM_NAME = "dormName";
    private static final String DORM_ROOM_NUMBER = "dormRoomNumber";
    private static final String REGISTRATION_TIME = "registrationTime";
    private static final String FULL_NAME = "fullName";
    private static final String USER_ID = "userID";
    private static final String USERS = "users";
    private static final String ADMIN = "admin";
    private static final String RATE_LIMIT = "rateLimit";

    public RedisDB(String host, int port, String dbEncryptionKey){
        RedisURI uri = RedisURI.create(host, port);
        client = RedisClient.create(uri);
        connection = client.connect();
        commands = connection.sync();
        this.dbEncryptionKey = dbEncryptionKey;
    }

    /*
        DB Commands With Encryption
     */
    private String AESEncrypt(String plaintext){
        // TODO: complete
        return plaintext;
    }

    private String AESDecrypt(String ciphertext){
        // TODO: complete
        return ciphertext;
    }

    // TODO: Encrypt all arguments to the commands.___ functions, then decrypt any Strings that are returned

    private long sadd(String key, String val){
        return commands.sadd(key, val);
    }

    private boolean hset(String key, String key1, String val){
        return commands.hset(key, key1, val);
    }

    private boolean sismember(String key, String val){
        return commands.sismember(key, val);
    }

    private Set<String> smembers(String key){
        return commands.smembers(key);
    }

    private String hget(String key, String key1){
        return commands.hget(key, key1);
    }

    private long del(String... keys){
        return commands.del(keys);
    }

    private long srem(String key, String val){
        return commands.srem(key, val);
    }






    public void createAccount(String username, String hashedPassword, String registrationTime, String salt,
                              String fullName, String studentId) throws UnsupportedEncodingException {
        sadd(USERS, username);
        hset(username, PASSWORD, hashedPassword);
        hset(username, SALT, salt);
        hset(username, ROOM_DRAW_NUMBER, "-1");
        hset(username, DORM_NAME, "-1");
        hset(username, DORM_ROOM_NUMBER, "-1");
        hset(username, REGISTRATION_TIME, registrationTime);
        hset(username, FULL_NAME, fullName);
        hset(username, USER_ID, studentId);
        hset(username, RATE_LIMIT, "0");
    }

    public boolean isAdmin(String username){
        return sismember(ADMIN, username);
    }

    public long addAdmin(String username) { return sadd(ADMIN, username); }

    public Set<String> getAdmin(){
        return smembers(ADMIN);
    }

    public Set<String> getUsers(){
        return smembers(USERS);
    }

    public void clearRoom(String dormName, String dormRoomNumber){
        String occupant;
        while (!(occupant = getOccupantOfRoom(dormName, dormRoomNumber)).equals("-1")){
            setDormName(occupant, "-1");
            setDormRoomNumber(occupant, "-1");
        }
    }

    //O(n) search for now
    public String getOccupantOfRoom(String dormName, String dormRoomNumber){
        Set<String> users = smembers(USERS);
        for (String user : users){
            String userDormName = hget(user, DORM_NAME);
            String userDormRoomNumber = hget(user, DORM_ROOM_NUMBER);
            if(userDormName.equals(dormName) && userDormRoomNumber.equals(dormRoomNumber)){
                return user;
            }
        }
        return "-1";
    }

    // Returns a space-separated string of room numbers that are occupied, within a given dorm
    public String getOccupiedRooms(String dormName){
        Set<String> users = smembers(USERS);
        String occupiedRooms = "";

        for (String user : users){
            String userDormName = hget(user, DORM_NAME);
            String userDormRoomNumber = hget(user, DORM_ROOM_NUMBER);
            if (userDormName.equals(dormName) && !userDormRoomNumber.equals("-1")){
                occupiedRooms += " " + userDormRoomNumber;
            }
        }
        return occupiedRooms;
    }

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
        return hget(username, USER_ID);
    }

    public void setUserID(String username, String userID){
        hset(username, USER_ID, userID);
    }

    public int getPacketCount(String clientId){
        return Integer.valueOf(hget(clientId, RATE_LIMIT));
    }

    public void setPacketCount(String clientId, int rateLimit){
        hset(clientId, RATE_LIMIT, ""+rateLimit);
    }

    public void clearRedisDB(){
        Set<String> users = getUsers();
        Iterator<String> usersIterator = users.iterator();
        while(usersIterator.hasNext()){
            String currentUser = usersIterator.next();
            del(currentUser);
            srem(USERS, currentUser);
        }
    }

    public void closeRedisConnection(){
        connection.close();
        client.shutdown();
    }
}
