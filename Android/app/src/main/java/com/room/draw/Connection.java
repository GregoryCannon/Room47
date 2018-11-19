package com.room.draw;


import android.content.Context;

import java.io.IOException;

public class Connection {
    public static SslClient client;

    public static ServerPacket login(String username, String password, Context context) throws IOException, ClassNotFoundException {
        SslServerHandler handler = (clientPacket) -> {
            return new ServerPacket("Generic message");
        };
        new Thread(() -> new SslServer(6667, handler)).start();
        client = new SslClient("localhost", 6667, context);
        ClientPacket testClientPacket = new ClientPacket(Action.REGISTER, username, password, "name", "dormName", "roomNumber");


        client.sendBytes(Serializer.serialize(testClientPacket));
        return client.readServerPacket();
    }

}
