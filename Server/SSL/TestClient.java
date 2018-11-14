import java.io.IOException;

/**
 * Created by Greg on 11/13/18.
 */
public class TestClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Initialize a client
        SslClient client = new SslClient("localhost", 9000);

        // Create a packet and send it
        ClientPacket msg = new ClientPacket(Action.REGISTER, null, null, "Greg", null);
        client.sendBytes(Serializer.serialize(msg));
        System.out.println("Sent packet.");

        // Read the server's response packet
        ServerPacket response = client.readServerPacket();
        System.out.println("Received server packet: " + response.response);
    }



    /*
        String msg = "Welcome to the test of the SSL protocol in Java";
        client.sendBytes(msg.getBytes());
        System.out.println("Sent: " + msg);

        String response = client.readString();
        System.out.println("Received: " + response);
    }
    */

}
