package ServerPackage;

public class AuditLogEntry {
    private static AuditLogDB.Action action;
    private static String username;
    private static String displacedStudent;
    private static double timestamp;

    public AuditLogEntry(String username, AuditLogDB.Action action, double timestamp){
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
        displacedStudent = "";
    }

    public AuditLogEntry(String username, AuditLogDB.Action action, double timestamp, String displacedStudent){
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
        this.displacedStudent = displacedStudent;
    }

    public AuditLogDB.Action getAction(){
        return action;
    }

    public String getUsername(){
        return username;
    }

    public String getDisplacedStudent(){
        return displacedStudent;
    }

    public double getTimestamp(){
        return timestamp;
    }

    public String toString(){
        return "Username: " + username + "/n" +
                "Action: " + action + "/n" +
                "timestamp: " + timestamp + "/n" +
                (displacedStudent.equals("") ? "" : "displacedStudent: " + displacedStudent);
    }
}
