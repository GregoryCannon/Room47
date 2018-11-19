package com.room.draw;

/**
 * Created by Greg on 11/14/18.
 */

import android.content.Context;

import java.io.IOException;


public class TestSsl {
    static SslServer server;
    static SslClient client;

    private static void init(SslServerHandler handler){
        // Initialize server
        new Thread(() -> { server = new SslServer(9000, handler); }).start();

        // Initialize a client
        client = new SslClient("localhost", 9000, null);
    }

    public void canSendPackets(Context context) throws IOException, ClassNotFoundException {
        // Setup
        ClientPacket testClientPacket = new ClientPacket(Action.REGISTER, "username", "password", "name", "dormName", "roomNumber");


        // Send packet and get server's response
        client.sendBytes(Serializer.serialize(testClientPacket));
        ServerPacket response = client.readServerPacket();


    }
}
