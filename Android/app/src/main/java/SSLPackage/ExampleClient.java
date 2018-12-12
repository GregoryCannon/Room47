package SSLPackage;

import java.io.IOException;

/**
 * Created by Greg on 11/13/18.
 */
public class ExampleClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Initialize server
        SslServerHandler handler = (clientPacket) -> {
            return new ServerPacket("Generic message");
        };
        new Thread(() -> new SslServer(9000, handler)).start();

        // Initialize a client
        SslClient client = new SslClient("localhost", 9000, null);

        // Create a packet and send it
        ClientPacket msg = new ClientPacket(Action.REGISTER, "josh", "12345678", "dormName", "roomNumber");
        client.sendClientPacket(msg);
        System.out.println("Sent packet.");

        // Read the server's message packet
        ServerPacket response = client.readServerPacket();
        System.out.println("Received server packet: " + response.message);
    }



    /*
        String msg = "Welcome to the test of the SSL protocol in Java";
        client.sendBytes(msg.getBytes());
        System.out.println("Sent: " + msg);

        String message = client.readString();
        System.out.println("Received: " + message);
    }
    */

}
