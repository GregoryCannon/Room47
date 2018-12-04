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
    private StudentDataManager studentDataManager;
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

    public RedisDB(String host, int port){
        RedisURI uri = RedisURI.create(host, port);
        client = RedisClient.create(uri);
        connection = client.connect();
        commands = connection.sync();
        studentDataManager = new StudentDataManager();
    }

    public void createAccount(String username, String hashedPassword, String registrationTime, String salt,
                              String fullName, String studentId) throws UnsupportedEncodingException {
        commands.sadd(USERS, username);
        commands.hset(username, PASSWORD, hashedPassword);
        commands.hset(username, SALT, salt);
        commands.hset(username, ROOM_DRAW_NUMBER, "-1");
        commands.hset(username, DORM_NAME, "-1");
        commands.hset(username, DORM_ROOM_NUMBER, "-1");
        commands.hset(username, REGISTRATION_TIME, registrationTime);
        commands.hset(username, FULL_NAME, fullName);
        commands.hset(username, USER_ID, studentId);
        commands.hset(username, RATE_LIMIT, "0");
    }

    public boolean isAdmin(String username){
        return commands.sismember(ADMIN, username);
    }

    public long addAdmin(String username) { return commands.sadd(ADMIN, username); }

    public Set<String> getAdmin(){
        return commands.smembers(ADMIN);
    }

    public Set<String> getUsers(){
        return commands.smembers(USERS);
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
        Set<String> users = commands.smembers(USERS);
        for (String user : users){
            String userDormName = commands.hget(user, DORM_NAME);
            String userDormRoomNumber = commands.hget(user, DORM_ROOM_NUMBER);
            if(userDormName.equals(dormName) && userDormRoomNumber.equals(dormRoomNumber)){
                return user;
            }
        }
        return "-1";
    }

    // Returns a space-separated string of room numbers that are occupied, within a given dorm
    public String getOccupiedRooms(String dormName){
        Set<String> users = commands.smembers(USERS);
        String occupiedRooms = "";

        for (String user : users){
            String userDormName = commands.hget(user, DORM_NAME);
            String userDormRoomNumber = commands.hget(user, DORM_ROOM_NUMBER);
            if (userDormName.equals(dormName) && !userDormRoomNumber.equals("-1")){
                occupiedRooms += " " + userDormRoomNumber;
            }
        }
        return occupiedRooms;
    }

    public String getHashedPassword(String username){
        return commands.hget(username, PASSWORD);
    }

    public void setRoomDrawNumber(String username, String roomDrawNumber){
        commands.hset(username, ROOM_DRAW_NUMBER, roomDrawNumber);
    }

    public String getRoomDrawNumber(String username){
        return commands.hget(username, ROOM_DRAW_NUMBER);
    }

    public void setDormName(String username, String dormRoom){
        commands.hset(username, DORM_NAME, dormRoom);
    }

    public String getDormName(String username){
        return commands.hget(username, DORM_NAME);
    }

    public void setDormRoomNumber(String username, String dormRoomNumber){
        commands.hset(username, DORM_ROOM_NUMBER, dormRoomNumber);
    }

    public String getDormRoomNumber(String username){
        return commands.hget(username, DORM_ROOM_NUMBER);
    }

    public String getSalt(String username){
        return commands.hget(username, SALT);
    }

    public void setSalt(String username, String salt){
        commands.hset(username, SALT, salt);
    }

    public String getRegistrationTime(String username){
        return commands.hget(username, REGISTRATION_TIME);
    }

    public void setRegistrationTime(String username, String registrationTime){
        commands.hset(username, REGISTRATION_TIME, registrationTime);
    }

    public String getFullName(String username){
        return commands.hget(username, FULL_NAME);
    }

    public void setFullName(String username, String fullName){
        commands.hset(username, FULL_NAME, fullName);
    }

    public String getUserID(String username){
        return commands.hget(username, USER_ID);
    }

    public void setUserID(String username, String userID){
        commands.hset(username, USER_ID, userID);
    }

    public int getPacketCount(String clientId){
        return Integer.valueOf(commands.hget(clientId, RATE_LIMIT));
    }

    public void setPacketCount(String clientId, int rateLimit){
        commands.hset(clientId, RATE_LIMIT, ""+rateLimit);
    }

    public void clearRedisDB(){
        Set<String> users = getUsers();
        Iterator<String> usersIterator = users.iterator();
        while(usersIterator.hasNext()){
            String currentUser = usersIterator.next();
            commands.del(currentUser);
            commands.srem(USERS, currentUser);
        }
    }

    public void closeRedisConnection(){
        connection.close();
        client.shutdown();
    }
}
