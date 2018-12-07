package SSLPackage;


import android.content.Context;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Connection {
    public static SslClient client;

    public static ServerPacket login(String username, String password, Context context) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        client = new SslClient("10.0.2.2", 6667, context);
        ClientPacket testClientPacket = new ClientPacket(Action.LOG_IN, username, password, "dormName", "userId");

        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket register(String username, String password, String userId, Context context) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        client = new SslClient("10.0.2.2", 6667, context);
        ClientPacket testClientPacket = new ClientPacket(Action.REGISTER, username, password, "dormName", userId);

        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket getInfo(String username, String password, Context context) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        ClientPacket testClientPacket = new ClientPacket(Action.GET_INFO, username, password, "dormName", "userId");

        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket requestRoom(String username, String password, String dorm, String roomNumber) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        ClientPacket testClientPacket = new ClientPacket(Action.REQUEST_ROOM, username, password, dorm, roomNumber);

        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }
}
