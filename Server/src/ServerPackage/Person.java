package ServerPackage;

import java.util.Date;

public class Person implements java.io.Serializable{
    private String status;
    public String ID;
    public String name;
    public String username;
    public String password;
    public int salt;
    public Date regTime;
    public int regNumber;
    public String room;
    static final String EMPTY_ROOM = "Empty";
    static final String UNASSIGNED = "None";

    public Person(String name, String username, String password, int salt, String ID, int regNumber, Date regTime){
        this.name = name;
        this.username = username;
        this.password = password;
        this.ID = ID;
        this.regTime = regTime;
        this.regNumber = regNumber;
        this.room = "None";
    }

    public String getStatus() {
        Date now = new Date();
        if (this.room != UNASSIGNED){
            return "Registered";
        } else if (now.compareTo(this.regTime) > 0) {
            return "Available to register";
        } else {
            return "Waiting to register";
        }
    }
}
