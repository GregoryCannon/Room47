package cryptography;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Greg on 10/17/18.
 * A project for CS181S, System Security
 */

public class Room47 {
    private static HashMap<String, String> assignments;
    private static HashMap<String, Person> people;
    static final String EMPTY_ROOM = "Empty";
    static final String UNASSIGNED = "None";

    public Room47(){

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
        Person newPerson = new Person(name, username, password, ID, regNumber, regTime);
        people.put(ID, newPerson);
    }

    public static boolean requestRoom(String room, String ID){
        Person person = people.get(ID);
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
        for (Person person: people.values()){
            person.room = UNASSIGNED;
        }
        for (String room: assignments.keySet()){
            if (assignments.get(room) != EMPTY_ROOM){
                Person owner = people.get(assignments.get(room));
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
            Person person = people.get(id);
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


