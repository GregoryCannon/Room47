package ServerPackage;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;


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

    public AuditLogDB(String host, int port){
        RedisURI uri = RedisURI.create(host, port);
        client = RedisClient.create(uri);
        connection = client.connect();
        commands = connection.sync();
    }

    public AuditLogEntry registerLog(String studentID, String studentUsername,
                                     String adminUsername, String displacedStudent, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.REGISTER, System.currentTimeMillis(),
                displacedStudent, dormName, dormName);
        if(!studentUsername.equals("")){
            commands.lpush(studentUsername, entry.toString());
        }
        if(!studentID.equals("")){
            commands.lpush(studentID, entry.toString());
        }
        if(!adminUsername.equals("")){
            commands.lpush(adminUsername, entry.toString());
        }
        if(!dormName.equals("")){
            commands.lpush(dormName, entry.toString());
        }
        if(!dormNumber.equals("")){
            commands.lpush(dormNumber, entry.toString());
        }
        return entry;
    }

    public AuditLogEntry loginLog(String studentID, String studentUsername,
                                  String adminUsername, String displacedStudent, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.LOGIN, System.currentTimeMillis(),
                displacedStudent, dormName, dormNumber);
        if(!studentUsername.equals("")){
            commands.lpush(studentUsername, entry.toString());
        }
        if(!studentID.equals("")){
            commands.lpush(studentID, entry.toString());
        }
        if(!adminUsername.equals("")){
            commands.lpush(adminUsername, entry.toString());
        }
        if(!dormName.equals("")){
            commands.lpush(dormName, entry.toString());
        }
        if(!dormNumber.equals("")){
            commands.lpush(dormNumber, entry.toString());
        }
        return entry;
    }

    public AuditLogEntry selectRoomLog(String studentID, String studentUsername,
                                       String adminUsername, String displacedStudent, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.SELECT_ROOM, System.currentTimeMillis(),
                displacedStudent, dormName, dormNumber);
        if(!studentUsername.equals("")){
            commands.lpush(studentUsername, entry.toString());
        }
        if(!studentID.equals("")){
            commands.lpush(studentID, entry.toString());
        }
        if(!adminUsername.equals("")){
            commands.lpush(adminUsername, entry.toString());
        }
        if(!dormName.equals("")){
            commands.lpush(dormName, entry.toString());
        }
        if(!dormNumber.equals("")){
            commands.lpush(dormNumber, entry.toString());
        }
        return entry;
    }

    /*
     *@param username - the username of the admin who displaced a student
     */
    public AuditLogEntry displaceStudentLog(String studentID, String studentUsername,
                                            String adminUsername, String displacedStudent, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.DISPLACE_STUDENT, System.currentTimeMillis(),
                displacedStudent, dormName, dormNumber);
        if(!studentUsername.equals("")){
            commands.lpush(studentUsername, entry.toString());
        }
        if(!studentID.equals("")){
            commands.lpush(studentID, entry.toString());
        }
        if(!adminUsername.equals("")){
            commands.lpush(adminUsername, entry.toString());
        }
        if(!dormName.equals("")){
            commands.lpush(dormName, entry.toString());
        }
        if(!dormNumber.equals("")){
            commands.lpush(dormNumber, entry.toString());
        }
        return entry;
    }

    public AuditLogEntry placeStudentLog(String studentID, String studentUsername,
                                            String adminUsername, String displacedStudent, String dormName, String dormNumber){
        AuditLogEntry entry = new AuditLogEntry(studentID, studentUsername, adminUsername, Action.PLACE_STUDENT, System.currentTimeMillis(),
                displacedStudent, dormName, dormNumber);
        if(!studentUsername.equals("")){
            commands.lpush(studentUsername, entry.toString());
        }
        if(!studentID.equals("")){
            commands.lpush(studentID, entry.toString());
        }
        if(!adminUsername.equals("")){
            commands.lpush(adminUsername, entry.toString());
        }
        if(!dormName.equals("")){
            commands.lpush(dormName, entry.toString());
        }
        if(!dormNumber.equals("")){
            commands.lpush(dormNumber, entry.toString());
        }
        return entry;
    }

    public void clearAuditLog(){
        commands.ltrim(STUDENT_USERNAME, 1,0);
        commands.ltrim(ADMIN_USERNAME, 1,0);
        commands.ltrim(STUDENT_ID, 1,0);
        commands.ltrim(DORM_NAME, 1,0);
        commands.ltrim(ROOM_NUMBER, 1,0);
    }

    public void closeRedisConnection(){
        connection.close();
        client.shutdown();
    }
}
