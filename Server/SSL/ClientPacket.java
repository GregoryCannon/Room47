/**
 * Created by Greg on 11/13/18.
 */
enum Action{
    REGISTER, LOG_IN, REQUEST_ROOM, GET_ROOMS, GET_INFO
}

public class ClientPacket implements java.io.Serializable{
    Action action;
    String username;
    String password;
    String name;
    String room;

    public ClientPacket(){}

    public ClientPacket(Action action, String username, String password, String name, String room){
        this.action = action;
        this.username = username;
        this.password = password;
        this.name = name;
        this.room = room;
    }

    @Override
    public boolean equals(Object o){
        ClientPacket other = (ClientPacket) o;
        return this.action.equals(other.action) && this.username.equals(other.username) && this.password.equals(other.password)
                && this.name.equals(other.name) && this.room.equals(other.room);
    }
}
