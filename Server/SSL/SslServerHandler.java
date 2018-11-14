/**
 * Created by Greg on 11/13/18.
 */
public interface SslServerHandler {
    ServerPacket handlePacket(ClientPacket p);
    //String handleString(String m);
}
