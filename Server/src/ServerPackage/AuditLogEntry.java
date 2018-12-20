package ServerPackage;

public class AuditLogEntry {
    private AuditLogDB.Action action;
    private String studentUsername;
    private String studentID;
    private String adminUsername;
    private String displacedStudent;
    private String dormName;
    private String dormNumber;
    private double timestamp;

    public String getDormNumber() {
        return dormNumber;
    }

    public void setDormNumber(String dormNumber) {
        this.dormNumber = dormNumber;
    }

    public AuditLogEntry(String studentID, String studentUsername, String adminUsername,
                         AuditLogDB.Action action, double timestamp, String displacedStudent, String dormName, String dormNumber){
        this.studentID = studentID;
        this.studentUsername = studentUsername;
        this.adminUsername = adminUsername;
        this.action = action;
        this.timestamp = timestamp;
        this.displacedStudent = displacedStudent;
        this.dormName = dormName;
        this.dormNumber = dormNumber;
    }

    public AuditLogDB.Action getAction(){
        return action;
    }

    public String getDisplacedStudent(){
        return displacedStudent;
    }

    public double getTimestamp(){
        return timestamp;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public String getDormName() {
        return dormName;
    }

    public void setDormName(String dormName) {
        this.dormName = dormName;
    }

    public void setDisplacedStudent(String displacedStudent) {
        this.displacedStudent = displacedStudent;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public void setAction(AuditLogDB.Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "AuditLogEntry{" +
                "action=" + action +
                ", studentUsername='" + studentUsername + '\'' +
                ", studentID='" + studentID + '\'' +
                ", adminUsername='" + adminUsername + '\'' +
                ", displacedStudent='" + displacedStudent + '\'' +
                ", dormName='" + dormName + '\'' +
                ", dormNumber='" + dormNumber + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
