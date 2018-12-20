package ServerPackage;

import SSLPackage.Serializer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//This class keeps track of all the audit logs of our service
//Audit logs are sorted by their timestamp (i.e., when the action was performed by a user or admin)
public class AuditLogDB {
    enum Action{
        REGISTER, LOGIN, SELECT_ROOM, DISPLACE_STUDENT, PLACE_STUDENT
    }
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private static final String STUDENT_USERNAME = "studentUsername";
    private static final String ADMIN_USERNAME = "adminUsername";
    private static final String STUDENT_ID = "studentID";
    private static final String DORM_NAME = "dormName";
    private static final String ROOM_NUMBER = "roomNumber";
    private static final String ENTRY_LIST = "entryList";

    public AuditLogDB(String host, int port){
        RedisURI uri = RedisURI.create(host, port);
        client = RedisClient.create(uri);
        connection = client.connect();
        commands = connection.sync();
    }

    private void lpush(String key, AuditLogEntry entry){
        String serialized = null;
        try {
            serialized = Base64.toBase64String(Serializer.serialize(entry));
        } catch (IOException e) {
            e.printStackTrace();
        }
        commands.lpush(ENTRY_LIST + key, serialized);
    }
    
    public List<AuditLogEntry> getLogsForStudent(String studentUsername) throws IOException, ClassNotFoundException {
        ArrayList<AuditLogEntry> studentLogs = new ArrayList<>();
        for(int i = 0; i<commands.llen(ENTRY_LIST + studentUsername); i++){
            String newLog = commands.lindex(ENTRY_LIST + studentUsername, i);
            byte[] raw = Base64.decode(newLog);
            studentLogs.add((AuditLogEntry) Serializer.deserialize(raw));
        }
        return studentLogs;
    }

    public List<AuditLogEntry> getLogsForAdmin(String adminUsername) throws IOException, ClassNotFoundException {
        ArrayList<AuditLogEntry> studentLogs = new ArrayList<>();
        for(int i = 0; i<commands.llen(ENTRY_LIST + adminUsername); i++){
            String newLog = commands.lindex(ENTRY_LIST + adminUsername, i);
            byte[] raw = Base64.decode(newLog);
            studentLogs.add((AuditLogEntry) Serializer.deserialize(raw));
        }
        return studentLogs;
    }

    public List<AuditLogEntry> getLogsForRoom(String dormName, String roomNumber) throws IOException, ClassNotFoundException {
        ArrayList<AuditLogEntry> studentLogs = new ArrayList<>();
        for(int i = 0; i<commands.llen(ENTRY_LIST + roomNumber); i++){
            String newLog = commands.lindex(ENTRY_LIST + roomNumber, i);
            byte[] raw = Base64.decode(newLog);
            AuditLogEntry newEntry = (AuditLogEntry) Serializer.deserialize(raw);
            if (newEntry.getDormName().equals(dormName)){
                studentLogs.add(newEntry);
            }
        }
        return studentLogs;
    }


    public void registerLog(String studentID, String studentUsername, String adminUsername,
                            String dormName, String dormNumber) throws IOException {
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.REGISTER, System.currentTimeMillis(),
                dormName, dormName);
        
        if(!studentUsername.equals("")){
            lpush(studentUsername, entry);
        }
        if(!studentID.equals("")){
            lpush(studentID, entry);
        }
        if(!adminUsername.equals("")){
            lpush(adminUsername, entry);
        }
        if(!dormNumber.equals("")){
            lpush(dormNumber, entry);
        }
    }

    public void loginLog(String studentID, String studentUsername,
                                  String adminUsername, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.LOGIN, System.currentTimeMillis(),
                dormName, dormNumber);
        if(!studentUsername.equals("")){
            lpush(studentUsername, entry);
        }
        if(!studentID.equals("")){
            lpush(studentID, entry);
        }
        if(!adminUsername.equals("")){
            lpush(adminUsername, entry);
        }
        if(!dormNumber.equals("")){
            lpush(dormNumber, entry);
        }
    }

    public void selectRoomLog(String studentID, String studentUsername,
                                       String adminUsername, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.SELECT_ROOM, System.currentTimeMillis(),
                dormName, dormNumber);
        if(!studentUsername.equals("")){
            lpush(studentUsername, entry);
        }
        if(!studentID.equals("")){
            lpush(studentID, entry);
        }
        if(!adminUsername.equals("")){
            lpush(adminUsername, entry);
        }
        if(!dormNumber.equals("")){
            lpush(dormNumber, entry);
        }
    }

    /*
     *@param username - the username of the admin who displaced a student
     */
    public void displaceStudentLog(String studentID, String studentUsername,
                                            String adminUsername, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.DISPLACE_STUDENT, System.currentTimeMillis(),
                dormName, dormNumber);
        if(!studentUsername.equals("")){
            lpush(studentUsername, entry);
        }
        if(!studentID.equals("")){
            lpush(studentID, entry);
        }
        if(!adminUsername.equals("")){
            lpush(adminUsername, entry);
        }
        if(!dormNumber.equals("")){
            lpush(dormNumber, entry);
        }
    }

    public void placeStudentLog(String studentID, String studentUsername,
                                            String adminUsername, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.PLACE_STUDENT, System.currentTimeMillis(),
                dormName, dormNumber);
        if(!studentUsername.equals("")){
            lpush(studentUsername, entry);
        }
        if(!studentID.equals("")){
            lpush(studentID, entry);
        }
        if(!adminUsername.equals("")){
            lpush(adminUsername, entry);
        }
        if(!dormNumber.equals("")){
            lpush(dormNumber, entry);
        }
    }

    public void clearAuditLog(){
        commands.ltrim(ENTRY_LIST + STUDENT_USERNAME, 1,0);
        commands.ltrim(ENTRY_LIST + ADMIN_USERNAME, 1,0);
        commands.ltrim(ENTRY_LIST + STUDENT_ID, 1,0);
        commands.ltrim(ENTRY_LIST + DORM_NAME, 1,0);
        commands.ltrim(ENTRY_LIST + ROOM_NUMBER, 1,0);
    }

    public void closeRedisConnection(){
        connection.close();
        client.shutdown();
    }
}
