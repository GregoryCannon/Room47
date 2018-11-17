package ServerPackage;

import SSLPackage.ClientPacket;
import SSLPackage.ServerPacket;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

public class Server {
    private static RedisDB redis;
    private static HashUtil hashUtil;
    private String authenticatedUser = null;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        new Server();
    }

    public Server() throws NoSuchAlgorithmException{
        hashUtil = new HashUtil();
        redis = new RedisDB("localhost", 6379, hashUtil);
    }

    public ServerPacket handle(ClientPacket p) {
        switch (p.action){
            case REGISTER:
                try {
                    registerUser(p.username, p.password, "00011122");
                    return new ServerPacket("Registration successful");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return new ServerPacket(e.getMessage());
                }
            case REQUEST_ROOM:
                if (authenticatedUser.equals(p.username)){
                    boolean success = requestRoom(p.dormName, p.roomNumber, p.username);
                    if (success){
                        return new ServerPacket("Room reserved!");
                    } else {
                        return new ServerPacket("Failed to reserve room. Check that the room is empty, " +
                            "and that you're currently available to register.");
                    }
                }
            case LOG_IN:
                if (authenticatedUser != null) {
                    return new ServerPacket("You're already logged in!");
                } else {
                    try {
                        if (logIn(p.username, p.password)){
                            return new ServerPacket("Login successful");
                        } else {
                            return new ServerPacket("Login failed");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return new ServerPacket(e.getMessage());
                    }
                }
            case ADMIN_PLACE_STUDENT:
                boolean success = adminPlaceUserInRoom(p.username, p.dormName, p.roomNumber);
                if (success) {
                    return new ServerPacket("User " + p.username + " placed in " + p.dormName + " " + p.roomNumber);
                } else {
                    return new ServerPacket("Unauthorized: you are not logged in as an administrator.");
                }
                break;
            case ADMIN_REMOVE_STUDENT:
                boolean success = adminRemoveUserFromRoom(p.dormName, p.roomNumber);
                if (success) {
                    String oldOccupant = redis.getOccupantOfRoom(p.dormName, p.roomNumber);
                    return new ServerPacket("User " + oldOccupant + " removed from " + p.dormName + " " + p.roomNumber);
                } else {
                    return new ServerPacket("Unauthorized: you are not logged in as an administrator.");
                }
            case GET_INFO:
                return new ServerPacket("That functionality is coming soon!");
            case GET_ROOMS:
                return new ServerPacket("That functionality is coming soon!");
        }
        return new ServerPacket("Unknown action requested");
    }

    public boolean adminPlaceUserInRoom(String studentUsername, String dormName, String dormRoomNumber){
        if (authenticatedUser == null || !redis.isAdmin(authenticatedUser)) return false;

        adminRemoveUserFromRoom(dormName, dormRoomNumber);

        // Add the specified person
        redis.setDormName(studentUsername, dormName);
        redis.setDormRoomNumber(studentUsername, dormRoomNumber);
        return true;
    }

    public boolean adminRemoveUserFromRoom(String dormName, String dormRoomNumber){
        if (authenticatedUser == null || !redis.isAdmin(authenticatedUser)) return false;

        // Kick out the current occupant if there is one
        String currentOccupant = redis.getOccupantOfRoom(dormName, dormRoomNumber);
        if (!currentOccupant.equals("-1")){
            redis.setDormName(currentOccupant, "-1");
            redis.setDormRoomNumber(currentOccupant, "-1");
        }
        return true;
    }

    public void registerUser(String username, String password, String studentID) throws UnsupportedEncodingException {
        String valid = "^[0-9]{8}$";
        if (studentID.matches(valid)) {
            Random rnd = new Random();
            // Get an Epoch value roughly between 1940 and 2010
            // -946771200000L = January 1, 1940
            // Add up to 70 years to it (using modulus on the next long)
            long ms = -946771200000L + (Math.abs(rnd.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000));
            String salt = "" + (int) (Math.random() * 999999);
            int regNumber = (int) (Math.random() * 1000);
            String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");
            redis.createAccount(username, hashedPassword, ""+ms, salt);
            redis.setRoomDrawNumber(username, "" + regNumber);
        }
    }

    public boolean logIn(String username, String password) throws UnsupportedEncodingException {
        String salt = redis.getSalt(username);
        if (salt == null) return false;
        String verificationHashPass = new String(hashUtil.hashPassword(salt, password), "UTF8");
        String redisHashedPassword = redis.getHashedPassword(username);
        if (redisHashedPassword == null) return false;
        if (redisHashedPassword.equals(verificationHashPass)){
            authenticatedUser = username;
            return true;
        } else {
            return false;
        }
    }

    public boolean requestRoom(String room, String roomNumber, String username){
        // TODO: Block two people in same room
        long currentTime = System.currentTimeMillis();
        long regTime = Long.parseLong(redis.getRegistrationTime(username));

        if (redis.getDormName(username).equals("-1") && redis.getDormRoomNumber(username).equals("-1")
                && regTime <= currentTime) {
            redis.setDormName(username, room);
            redis.setDormRoomNumber(username, roomNumber);
            return true;
        }
        return false;
    }
}
