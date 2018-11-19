package com.room.draw;


import android.content.Context;

import java.io.IOException;

public class Connection {
    public static SslClient client;

    public static ServerPacket login(String username, String password, Context context) throws IOException, ClassNotFoundException {
//        SslServerHandler handler = (clientPacket) -> {
//            return new ServerPacket("Generic message");
//        };
//        new Thread(() -> new SslServer(9000, handler)).start();
        client = new SslClient("10.0.2.2", 9000, context);
        ClientPacket testClientPacket = new ClientPacket(Action.REGISTER, username, password, "dormName", "roomNumber");


        client.sendClientPacket(testClientPacket);
        return client.readServerPacket();
    }

}
