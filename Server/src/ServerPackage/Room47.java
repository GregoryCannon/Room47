package ServerPackage;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Room47 {
		
	private static RedisDB redis;
    	private static HashUtil hashUtil;
    
	public static void setUp() throws NoSuchAlgorithmException { 
		hashUtil = new HashUtil();
		redis = new RedisDB("localhost", 6379, hashUtil);
		
	}
	 
	public void setRoomDrawNumber() {
        String username = "Sam";
        String roomDrawNumber = "1234";
        redis.setRoomDrawNumber(username, roomDrawNumber);
        
	}
	
	public static void requestRoom(String room, String roomNumber, String username){
		if (redis.getDormRoom(username).equals("-1") && redis.getDormRoomNumber(username).equals("-1")) {
			redis.setDormRoom(username, room);
			redis.setDormRoomNumber(username, roomNumber);
		}
    	}
	
	public static void main(String[] args){
	try {
		setUp();
	}
	catch (NoSuchAlgorithmException e) {
			
	}
	String username = "Sam";
        String password = "passphrase";
        String salt = "123456";
        String registrationTime = "98765";
        String roomDrawNumber = "1234";
        String username2 = "Sam2"; 
        String password2 = "passphrase2";
        String salt2 = "123455";
        String registrationTime2 = "98766";
        String roomDrawNumber2 = "1235";
        String username3 = "Sam3"; 
        String password3 = "passphrase3";
        String salt3 = "123454";
        String registrationTime3 = "98767";
        String roomDrawNumber3 = "1236";
        String username4 = "Sam4"; 
        String password4 = "passphrase4";
        String salt4 = "123453";
        String registrationTime4 = "98768";
        String roomDrawNumber4 = "1237";
        String username5 = "Sam5"; 
        String password5 = "passphrase5";
        String salt5 = "123452";
        String registrationTime5 = "98769";
        String roomDrawNumber5 = "1238";
        try {
        redis.createAccount(username, password, registrationTime, salt);
        redis.createAccount(username2, password2, registrationTime2, salt2);
        redis.createAccount(username3, password3, registrationTime3, salt3);
        redis.createAccount(username4, password4, registrationTime4, salt4);
        redis.createAccount(username5, password5, registrationTime5, salt5);
        redis.setRoomDrawNumber(username, roomDrawNumber);
        redis.setRoomDrawNumber(username2, roomDrawNumber2);
        redis.setRoomDrawNumber(username3, roomDrawNumber3);
        redis.setRoomDrawNumber(username4, roomDrawNumber4);
        redis.setRoomDrawNumber(username5, roomDrawNumber5);
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");
        String hashedPassword2 = new String(hashUtil.hashPassword(salt2, password2), "UTF8");
        String hashedPassword3 = new String(hashUtil.hashPassword(salt3, password3), "UTF8");
        String hashedPassword4 = new String(hashUtil.hashPassword(salt4, password4), "UTF8");
        String hashedPassword5 = new String(hashUtil.hashPassword(salt5, password5), "UTF8");
        }
        catch (UnsupportedEncodingException e) {
        	
        }
        requestRoom("Clark V", "117", username);
        requestRoom("Clark I", "117", username2);
        requestRoom("Walker", "204", username3);
        requestRoom("Walker", "204", username4);
        requestRoom("Walker", "208", username5);
        
        System.out.println(username);
		System.out.println(redis.getDormRoom(username) + redis.getDormRoomNumber(username));
		System.out.println(username2);
		System.out.println(redis.getDormRoom(username2) + redis.getDormRoomNumber(username2));
		System.out.println(username3);
		System.out.println(redis.getDormRoom(username3) + redis.getDormRoomNumber(username3));
		System.out.println(username4);
		System.out.println(redis.getDormRoom(username4) + redis.getDormRoomNumber(username4));
		System.out.println(username5);
		System.out.println(redis.getDormRoom(username5) + redis.getDormRoomNumber(username5));
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


public class ServerPackage.Room47 {
    private static HashMap<String, String> assignments;
    private static HashMap<String, ServerPackage.Person> people;
    static final String EMPTY_ROOM = "Empty";
    static final String UNASSIGNED = "None";

    public ServerPackage.Room47(){

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

    public static boolean requestRoom(String room, String ID){
        ServerPackage.Person person = people.get(ID);
        if (assignments.containsKey(room) && !assignments.get(room).equals(EMPTY_ROOM)){
            return false;
        }
        if (person.room != UNASSIGNED){
            return false;
        }
        assignments.put(room, ID);
        person.room = room;
        return true;
    }

    public static void syncData(){
        for (ServerPackage.Person person: people.values()){
            person.room = UNASSIGNED;
        }
        for (String room: assignments.keySet()){
            if (assignments.get(room) != EMPTY_ROOM){
                ServerPackage.Person owner = people.get(assignments.get(room));
                owner.room = room;
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

        for (String room: assignments.keySet()){
            System.out.println(room + " - " + assignments.get(room));
        }

        for (String id : people.keySet()){
            ServerPackage.Person person = people.get(id);
            System.out.println(person.ID);
            System.out.println("\t" + person.name);
            System.out.println("\t" + person.getStatus());
            System.out.println("\t" + person.room);
            System.out.println("\t" + person.username);
            System.out.println("\t" + person.password);
            System.out.println("\t" + person.regNumber);
            System.out.println("\t" + person.regTime);
        }
    }


}

*/
