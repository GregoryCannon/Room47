package ServerPackage;

import SSLPackage.ClientPacket;
import SSLPackage.ServerPacket;
import SSLPackage.SslServer;
import SSLPackage.SslServerHandler;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static SSLPackage.ServerPacket.*;

public class Server {
    public ServerActor actor;
    private static SslServer sslServer;

    private String authenticatedUser = null;

    public static final int RATE_LIMIT = 1000;
    private int preLoginPacketCount = 0;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Server server = new Server();
        SslServerHandler handler = server::handle;
        sslServer = new SslServer(6667, handler);
    }

    public Server() throws NoSuchAlgorithmException{
        actor = new ServerActor();
    }

    public ServerPacket handle(ClientPacket p) {
        // Rate limiting
        int packetCount = updatePacketCount();
        if (packetCount > RATE_LIMIT){
            closeSslSocket();
            return new ServerPacket(RATE_LIMIT_REACHED);
        }

        switch (p.action){
            case REGISTER: return register(p);
            case REQUEST_ROOM: return requestRoom(p);
            case LOG_IN: return logIn(p);
            case LOG_OUT: return logOut(p);
            case ADMIN_PLACE_STUDENT: return adminPlaceStudent(p);
            case ADMIN_REMOVE_STUDENT: return adminRemoveStudent(p);
            case GET_INFO: return getInfo(p);
            case GET_ROOMS: return getOccupiedRooms(p);
        }
        return new ServerPacket(UNKNOWN_ACTION);
    }

    private int updatePacketCount(){
        // If not logged in, locally track packet count
        if (authenticatedUser == null){
            preLoginPacketCount += 1;
            return preLoginPacketCount;
        }
        // Otherwise, query the database for that user's packet count
        else {
            return actor.getAndIncrementPacketCount(authenticatedUser);
        }
    }

    private void closeSslSocket(){
        if (sslServer != null){     // sslServer is null during unit tests
            sslServer.close();
        }
    }

    private ServerPacket register(ClientPacket p){
        try {
            boolean regSuccess = actor.registerUser(p.username, p.password, p.roomNumber, false);
            boolean loginSuccess = actor.logIn(p.username, p.password);
            if (regSuccess && loginSuccess){
                return new ServerPacket(REGISTRATION_SUCCESSFUL);
            }
            return new ServerPacket(REGISTRATION_FAILED);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ServerPacket(e.getMessage());
        }
    }

    private ServerPacket requestRoom(ClientPacket p){
        if (authenticatedUser != null){
            boolean success = actor.requestRoom(authenticatedUser, p.dormName, p.roomNumber);
            if (success){
                return new ServerPacket(RESERVE_SUCCESSFUL);
            } else {
                return new ServerPacket(RESERVE_FAILED);
            }
        } else {
            return new ServerPacket(NOT_LOGGED_IN);
        }
    }

    private ServerPacket logIn(ClientPacket p){
        if (authenticatedUser != null) {
            return new ServerPacket(ALREADY_LOGGED_IN);
        } else {
            try {
                if (actor.logIn(p.username, p.password)){
                    authenticatedUser = p.username;
                    return new ServerPacket(LOGIN_SUCCESSFUL);
                } else {
                    return new ServerPacket(LOGIN_FAILED);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return new ServerPacket(e.getMessage());
            }
        }
    }

    private ServerPacket logOut(ClientPacket p){
        if (authenticatedUser == null) {
            return new ServerPacket(NOT_LOGGED_IN);
        } else {
            authenticatedUser = null;
            return new ServerPacket(LOGOUT_SUCCESSFUL);
        }
    }

    private ServerPacket adminPlaceStudent(ClientPacket p){
        if (authenticatedUser == null || !actor.isAdmin(authenticatedUser)){
            return new ServerPacket(ADMIN_UNAUTHORIZED);
        } else {
            boolean success = actor.adminPlaceUserInRoom(p.username, p.dormName, p.roomNumber);
            if (success) {
                return new ServerPacket(PLACE_STUDENT_SUCCESSFUL);
            } else {
                return new ServerPacket(PLACE_STUDENT_FAILED);
            }
        }
    }

    private ServerPacket adminRemoveStudent(ClientPacket p){
        if (authenticatedUser == null || !actor.isAdmin(authenticatedUser)){
            return new ServerPacket(ADMIN_UNAUTHORIZED);
        } else {
            boolean success = actor.adminRemoveUserFromRoom(p.dormName, p.roomNumber);
            if (success) {
                return new ServerPacket(REMOVE_STUDENT_SUCCESSFUL);
            } else {
                return new ServerPacket(REMOVE_STUDENT_FAILED);
            }
        }
    }

    private ServerPacket getInfo(ClientPacket p){
        if (authenticatedUser != null){
            String info = actor.getInfo(authenticatedUser);
            if (info.equals(GET_INFO_FAILED)){
                return new ServerPacket(GET_INFO_FAILED);
            } else {
                return new ServerPacket(info);
            }
        } else {
            return new ServerPacket(NOT_LOGGED_IN);
        }
    }

    private ServerPacket getOccupiedRooms(ClientPacket p){
        return new ServerPacket(actor.getOccupiedRooms(p.dormName));
    }
}
