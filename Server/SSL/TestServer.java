/**
 * Created by Greg on 11/13/18.
 */
public class TestServer {
    public static void main(String[] args){
        SslServerHandler handler = (clientPacket) -> {
            String message = "Generic response";
            return new ServerPacket(message);
        };
        SslServer server = new SslServer(9000, handler);
    }
}
