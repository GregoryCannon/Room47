package SSLPackage;

/**
 * Created by Greg on 11/13/18.
 */
public class ServerPacket implements java.io.Serializable{
    public String message;

    public ServerPacket(String response){
        this.message = response;
    }
}
