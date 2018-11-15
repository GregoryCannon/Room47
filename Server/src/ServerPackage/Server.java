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
    private boolean isAuthenticated = false;

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
                    registerUser(p.username, p.password);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return new ServerPacket(e.getMessage());
                }
                break;
            case REQUEST_ROOM:
                if (isAuthenticated){
                    boolean success = requestRoom(p.dormName, p.roomNumber, p.username);
                    if (success){
                        return new ServerPacket("Room reserved!");
                    } else {
                        return new ServerPacket("Failed to reserve room. Check that the room is empty, " +
                            "and that you're currently available to register.");
                    }
                }
            case LOG_IN:
                if (isAuthenticated) {
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
            case GET_INFO:
                return new ServerPacket("That functionality is coming soon!");
            case GET_ROOMS:
                return new ServerPacket("That functionality is coming soon!");
        }
        return new ServerPacket("Unknown action requested");
    }

    public void registerUser(String username, String password) throws UnsupportedEncodingException {
        // TODO: Check for valid pomona ID #
        // TODO: Store names
        Random rnd;
        Date regTime;
        long ms;

        // Get a new random instance, seeded from the clock
        rnd = new Random();

        // Get an Epoch value roughly between 1940 and 2010
        // -946771200000L = January 1, 1940
        // Add up to 70 years to it (using modulus on the next long)
        ms = -946771200000L + (Math.abs(rnd.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000));

        // Construct a date
        regTime = new Date(ms);
        String salt = "" + (int) (Math.random()*999999);
        int regNumber = (int) (Math.random()*1000);
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");
        redis.createAccount(username, hashedPassword, regTime.toString(), salt);
        redis.setRoomDrawNumber(username, ""+regNumber);
    }

    public boolean logIn(String username, String password) throws UnsupportedEncodingException {
        String salt = redis.getSalt(username);
        if (salt == null) return false;
        String verificationHashPass = new String(hashUtil.hashPassword(salt, password), "UTF8");
        String redisHashedPassword = redis.getHashedPassword(username);
        if (redisHashedPassword == null) return false;
        isAuthenticated = redisHashedPassword.equals(verificationHashPass);
        return isAuthenticated;
    }

    public boolean requestRoom(String room, String roomNumber, String username){
        // TODO: Block two people in same room
        // TODO: Check peoples' registration times

        if (redis.getDormRoom(username).equals("-1") && redis.getDormRoomNumber(username).equals("-1")) {
            redis.setDormRoom(username, room);
            redis.setDormRoomNumber(username, roomNumber);
            return true;
        }
        return false;
    }
}








/**
 package cryptography;
 import java.io.BufferedReader;
 import java.io.FileReader;
 import java.util.Date;
 import java.util.HashMap;


 * Created by Greg on 10/17/18.
 * A project for CS181S, System Security


 public class ServerPackage.Server {
 private static HashMap<String, String> assignments;
 private static HashMap<String, ServerPackage.Person> people;
 static final String EMPTY_ROOM = "Empty";
 static final String UNASSIGNED = "None";

 public ServerPackage.Server(){

 }

 public static void readRooms() {
 String file = "src/rooms.txt";

 try {
 BufferedReader reader = new BufferedReader(new FileReader(file));
 String currentLine = reader.readLine();
 while (currentLine != null){
 assignments.put(currentLine, "Empty");
 currentLine = reader.readLine();
 }
 reader.close();
 } catch (Exception e) {
 e.printStackTrace();
 }
 }

 public static void register(String name, String username, String password, String ID){
 final long HOUR = 3600 * 1000;
 //Date regTime = new Date((new Date()).getTime() + 2 * HOUR);
 Date regTime = new Date();
 int regNumber = people.size() + 10;
 ServerPackage.Person newPerson = new ServerPackage.Person(name, username, password, ID, regNumber, regTime);
 people.put(ID, newPerson);
 }

 public static boolean requestRoom(String dormName, String ID){
 ServerPackage.Person person = people.get(ID);
 if (assignments.containsKey(dormName) && !assignments.get(dormName).equals(EMPTY_ROOM)){
 return false;
 }
 if (person.dormName != UNASSIGNED){
 return false;
 }
 assignments.put(dormName, ID);
 person.dormName = dormName;
 return true;
 }

 public static void syncData(){
 for (ServerPackage.Person person: people.values()){
 person.dormName = UNASSIGNED;
 }
 for (String dormName: assignments.keySet()){
 if (assignments.get(dormName) != EMPTY_ROOM){
 ServerPackage.Person owner = people.get(assignments.get(dormName));
 owner.dormName = dormName;
 }
 }
 }

 public static void main(String[] args){
 assignments = new HashMap<>();
 people = new HashMap<>();

 readRooms();
 register("Greg Cannon", "gccc2015", "bananas", "10344");
 register("Jake Smith", "jhss2015", "apple", "10891");
 register("Eric Gofen", "ejgf2015", "snack", "10420");
 register("Cris Woroch", "cpww2015", "gains", "99999");

 requestRoom("Clark V 117", "10891");
 requestRoom("Clark I 117", "10344");
 requestRoom("Walker 204", "10344");
 requestRoom("Walker 204", "99999");
 syncData();

 for (String dormName: assignments.keySet()){
 System.out.println(dormName + " - " + assignments.get(dormName));
 }

 for (String id : people.keySet()){
 ServerPackage.Person person = people.get(id);
 System.out.println(person.ID);
 System.out.println("\t" + person.name);
 System.out.println("\t" + person.getStatus());
 System.out.println("\t" + person.dormName);
 System.out.println("\t" + person.username);
 System.out.println("\t" + person.password);
 System.out.println("\t" + person.regNumber);
 System.out.println("\t" + person.regTime);
 }
 }


 }

 */
