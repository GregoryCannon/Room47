package SSLPackage;


import android.content.Context;

import java.io.IOException;

public class Connection {
    public static SslClient client;

    public static ServerPacket login(String username, String password, Context context) throws IOException, ClassNotFoundException {
        client = new SslClient("10.0.2.2", 6667, context);
        ClientPacket testClientPacket = new ClientPacket(Action.LOG_IN, username, password, "dormName", "roomNumber");

        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket register(String username, String password, String userId, Context context) throws IOException, ClassNotFoundException {
        client = new SslClient("10.0.2.2", 6667, context);
        ClientPacket testClientPacket = new ClientPacket(Action.REGISTER, username, password, "dormName", userId);

        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }
}
