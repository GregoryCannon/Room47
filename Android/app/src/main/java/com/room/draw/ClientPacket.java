package com.room.draw;

public class ClientPacket implements java.io.Serializable{
    public Action action;
    public String username;
    public String password;
    public String name;
    public String dormName;
    public String roomNumber;

    public ClientPacket(){}

    public ClientPacket(Action action, String username, String password, String name, String dormName, String roomNumber){
        this.action = action;
        this.username = username;
        this.password = password;
        this.name = name;
        this.dormName = dormName;
        this.roomNumber = roomNumber;
    }

    @Override
    public boolean equals(Object o){
        ClientPacket other = (ClientPacket) o;
        return this.action.equals(other.action) && this.username.equals(other.username) && this.password.equals(other.password)
                && this.name.equals(other.name) && this.dormName.equals(other.dormName);
    }
}
