import cryptography.HashUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.UnsupportedEncodingException;

public class RedisDB {
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private HashUtil hashUtil;
    private static final String PASSWORD = "password";
    private static final String SALT = "salt";
    private static final String ROOM_DRAW_NUMBER = "roomDrawNumber";
    private static final String DORM_ROOM = "dormRoom";
    private static final String DORM_ROOM_NUMBER = "dormRoomNumber";
    private static final String REGISTRATION_TIME = "registrationTime";
    public RedisDB(String host, int port, HashUtil hashUtil){
        client = RedisClient.create(RedisURI.create(host, port));
        connection = client.connect();
        commands = connection.sync();
        this.hashUtil = hashUtil;
    }

    public void createAccount(String username, String password, String registrationTime, String salt) throws UnsupportedEncodingException {
        byte[] hashedPassword = hashUtil.hashPassword(salt, password);
        String convertedPassword = new String(hashedPassword, "UTF8");
        commands.hset(username, PASSWORD, convertedPassword);
        commands.hset(username, SALT, salt);
        commands.hset(username, ROOM_DRAW_NUMBER, "-1");
        commands.hset(username, DORM_ROOM, "-1");
        commands.hset(username, DORM_ROOM_NUMBER, "-1");
        commands.hset(username, REGISTRATION_TIME, registrationTime);
    }

    public String getPassword(String username){
        return commands.hget(username, PASSWORD);
    }

    public void setRoomDrawNumber(String username, String roomDrawNumber){
        commands.hset(username, ROOM_DRAW_NUMBER, roomDrawNumber);
    }

    public String getRoomDrawNumber(String username){
        return commands.hget(username, ROOM_DRAW_NUMBER);
    }

    public void setDormRoom(String username, String dormRoom){
        commands.hset(username, DORM_ROOM, dormRoom);
    }

    public String getDormRoom(String username){
        return commands.hget(username, DORM_ROOM);
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
    
    public boolean isAuthenticated(String username, String salt, String password) throws UnsupportedEncodingException {
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");
        return commands.hget(username, PASSWORD).equals(hashedPassword);
    }

    public void closeRedisConnection(){
        connection.close();
        client.shutdown();
    }
}
