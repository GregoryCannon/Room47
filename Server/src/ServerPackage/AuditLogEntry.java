package ServerPackage;

import java.util.Date;

public class AuditLogEntry implements java.io.Serializable {
    private AuditLogDB.Action action;
    private String studentUsername;
    private String studentID;
    private String adminUsername;
    private String displacedStudent;
    private String dormName;
    private String dormNumber;
    private long timestamp;

    private static final long serialVersionUID = 11112222L;

    public String getDormNumber() {
        return dormNumber;
    }

    public void setDormNumber(String dormNumber) {
        this.dormNumber = dormNumber;
    }

    public AuditLogEntry(String studentID, String studentUsername, String adminUsername,
                         AuditLogDB.Action action, long timestamp, String dormName, String dormNumber){
        this.studentID = studentID;
        this.studentUsername = studentUsername;
        this.adminUsername = adminUsername;
        this.action = action;
        this.timestamp = timestamp;
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

    public void setTimestamp(long timestamp) {
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
                "\n\t action=" + action +
                ",\n\t studentUsername='" + studentUsername + '\'' +
                ",\n\t studentID='" + studentID + '\'' +
                ",\n\t adminUsername='" + adminUsername + '\'' +
                ",\n\t dormName='" + dormName + '\'' +
                ",\n\t dormNumber='" + dormNumber + '\'' +
                ",\n\t timestamp=" + new Date(timestamp) +
                '}';
    }
}
