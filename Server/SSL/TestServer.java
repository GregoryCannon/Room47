/**
 * Created by Greg on 11/13/18.
 */
public class TestServer {
    public static void main(String[] args){
        /* All the server needs is a port and a handler method.
            The handler method receives a client packet, then does any backend updates, and returns a message packet.
        */
        SslServerHandler handler = (clientPacket) -> {
            return new ServerPacket("Generic message");
        };
        SslServer server = new SslServer(9000, handler);
    }
}
