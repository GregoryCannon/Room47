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
    private int preLoginPacketCount = 0;
    public static final int RATE_LIMIT = 1000;

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
        int rateLimit;
        if (authenticatedUser == null){
            preLoginPacketCount += 1;
            rateLimit = preLoginPacketCount;
        } else {
            rateLimit = actor.getAndIncrementPacketCount(authenticatedUser);
        }
        if (rateLimit > RATE_LIMIT) {
            return new ServerPacket(RATE_LIMIT_REACHED);
        }

        switch (p.action){
            case REGISTER:
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

            case REQUEST_ROOM:
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

            case LOG_IN:
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

            case LOG_OUT:
                if (authenticatedUser == null) {
                    return new ServerPacket(NOT_LOGGED_IN);
                } else {
                    authenticatedUser = null;
                    return new ServerPacket(LOGOUT_SUCCESSFUL);
                }

            case ADMIN_PLACE_STUDENT:
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

            case ADMIN_REMOVE_STUDENT:
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

            case GET_INFO:
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

            case GET_ROOMS:
                return new ServerPacket(actor.getOccupiedRooms(p.dormName));
        }
        return new ServerPacket(UNKNOWN_ACTION);
    }


}
