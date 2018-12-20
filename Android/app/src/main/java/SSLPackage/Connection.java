package SSLPackage;


import android.content.Context;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Connection {
    private static boolean handshakeComplete;
    public static SslClient client;

    public static ServerPacket login(String username, String password, Context context) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        if (!handshakeComplete) {
            client = new SslClient("10.0.2.2", 6667, context);
        }
        handshakeComplete = true;
        ClientPacket testClientPacket = new ClientPacket(Action.LOG_IN, username, password, "dormName", "userId");
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket logout(String username, String password, Context context) throws IOException, ClassNotFoundException {
        ClientPacket testClientPacket = new ClientPacket(Action.LOG_OUT, username, password, "dormName", "userId");
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket register(String username, String password, String userId, Context context) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        if (!handshakeComplete) {
            client = new SslClient("10.0.2.2", 6667, context);
        }
        handshakeComplete = true;
        ClientPacket testClientPacket = new ClientPacket(Action.REGISTER, username, password, "dormName", userId);
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket getInfo(String username, String password, Context context) throws IOException, ClassNotFoundException {
        ClientPacket testClientPacket = new ClientPacket(Action.GET_INFO, username, password, "dormName", "userId");
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket requestRoom(String username, String password, String dorm, String roomNumber) throws IOException, ClassNotFoundException {
        ClientPacket testClientPacket = new ClientPacket(Action.REQUEST_ROOM, username, password, dorm, roomNumber);
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket getOccupiedRooms(String username, String password, String dorm) throws IOException, ClassNotFoundException {
        ClientPacket testClientPacket = new ClientPacket(Action.GET_ROOMS, username, password, dorm, "roomNumber");
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket placeStudentInRoom(String username, String dorm, String room) throws IOException, ClassNotFoundException {
        ClientPacket testClientPacket = new ClientPacket(Action.ADMIN_PLACE_STUDENT, username, "password", dorm, room);
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket removeStudentFromRoom(String dorm, String room) throws IOException, ClassNotFoundException {
        ClientPacket testClientPacket = new ClientPacket(Action.ADMIN_REMOVE_STUDENT, "username", "password", dorm, room);
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

    public static ServerPacket requestTempPassword(String username, Context context) throws IOException, ClassNotFoundException {
        if (!handshakeComplete) {
            client = new SslClient("10.0.2.2", 6667, context);
        }
        ClientPacket testClientPacket = new ClientPacket(Action.REQUEST_TEMP_PASSWORD, username, "password", "dorm", "room");
        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }
}
