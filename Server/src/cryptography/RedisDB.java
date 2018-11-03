import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisDB {
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private static final String PASSWORD = "password";
    private static final String ROOM_DRAW_NUMBER = "roomDrawNumber";
    private static final String DORM_ROOM = "dormRoom";
    private static final String DORM_ROOM_NUMBER = "dormRoomNumber";
    public RedisDB(String host, int port){
        client = RedisClient.create(RedisURI.create(host, port));
        connection = client.connect();
        commands = connection.sync();
    }

    public void createAccount(String username, String password){
        commands.hset(username, PASSWORD, password);
        commands.hset(username, ROOM_DRAW_NUMBER, "-1");
        commands.hset(username, DORM_ROOM, "-1");
        commands.hset(username, DORM_ROOM_NUMBER, "-1");
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

    public void closeRedisConnection(){
        connection.close();
        client.shutdown();
    }
}
