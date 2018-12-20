package SSLPackage;

/**
 * Created by Greg on 11/14/18.
 */
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class TestSsl {
    static SslServer server;
    static SslClient client;
    static Thread serverThread;

    private final ClientPacket testClientPacket = new ClientPacket(Action.REGISTER, "username", "password", "dormName", "roomNumber");
    private final String testResponse = "server response";

    private static void createEchoServer(SslServerHandler handler, int port){
        // Initialize server
        serverThread = new Thread(() -> { server = new SslServer(port, handler); });
        serverThread.start();

        // Initialize a client
        client = new SslClient("localhost", port);
    }

    @After
    public void killServer(){

        if (serverThread != null) serverThread.stop();
    }

    @Test
    public void canSendPacketAsBytes() throws IOException, ClassNotFoundException {
        // Setup
        createEchoServer((clientPacket) -> {
            // Check that all info was preserved
            assertEquals(clientPacket, testClientPacket);
            return new ServerPacket(testResponse);
        }, 9000);

        // Send packet and get server's response
        client.sendBytes(Serializer.serialize(testClientPacket));
        ServerPacket response = client.readServerPacket();

        assertEquals(response.message, testResponse);
    }

    @Test
    public void canSendClientPacket() throws IOException, ClassNotFoundException {
        // Setup
        createEchoServer((clientPacket) -> {
            // Check that all info was preserved
            assertEquals(clientPacket, testClientPacket);
            return new ServerPacket(testResponse);
        }, 9001);

        client.sendClientPacket(testClientPacket);
        ServerPacket response = client.readServerPacket();

        assertEquals(response.message, testResponse);
    }
}