package SSLPackage;

/**
 * Created by Greg on 11/13/18.
 */
public class ExampleServer {
    public static void main(String[] args){
        /* All the server needs is a port and a handler method.
            The handler method receives a client packet, then does any backend updates, and returns a message packet.
        */
        SslServerHandler handler = (clientPacket) -> {

            // Backend logic goes here

            return new ServerPacket("Generic message");
        };
        SslServer server = new SslServer(6667, handler);
    }
}
