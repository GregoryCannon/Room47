package SSLPackage;

/**
 * Created by Greg on 11/14/18.
 */
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class TestSsl {
    static SslServer server;
    static SslClient client;

    private static void init(SslServerHandler handler){
        // Initialize server
        new Thread(() -> { server = new SslServer(9000, handler); }).start();

        // Initialize a client
        client = new SslClient("localhost", 9000, null);
    }

    @Test
    public void canSendPackets() throws IOException, ClassNotFoundException {
        // Setup
        ClientPacket testClientPacket = new ClientPacket(Action.REGISTER, "username", "password", "dormName", "roomNumber");
        String testResponse = "server response";
        init((clientPacket) -> {
            // Check that all info was preserved
            assertEquals(clientPacket, testClientPacket);
            return new ServerPacket(testResponse);
        });

        // Send packet and get server's response
        client.sendBytes(Serializer.serialize(testClientPacket));
        ServerPacket response = client.readServerPacket();

        assertEquals(response.message, testResponse);
    }
}