package ServerPackage;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;


//This class keeps track of all the audit logs of our service
//Audit logs are sorted by their timestamp (i.e., when the action was performed by a user or admin)
public class AuditLogDB {
    enum Action{
        REGISTER, LOGIN, SELECT_ROOM, DISPLACE_STUDENT
    }
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private static final String AUDIT_LOG = "auditLog";

    public AuditLogDB(String host, int port){
        RedisURI uri = RedisURI.create(host, port);
        client = RedisClient.create(uri);
        connection = client.connect();
        commands = connection.sync();
    }

    public AuditLogEntry registerLog(String username){
        double timestamp = System.currentTimeMillis();
        Action action = Action.REGISTER;
        AuditLogEntry entry = new AuditLogEntry(username, action, timestamp);
        commands.zadd(AUDIT_LOG, timestamp, entry.toString());
        return entry;
    }

    public AuditLogEntry loginLog(String username){
        double timestamp = System.currentTimeMillis();
        Action action = Action.LOGIN;
        AuditLogEntry entry = new AuditLogEntry(username, action, timestamp);
        commands.zadd(AUDIT_LOG, timestamp, entry.toString());
        return entry;
    }

    public AuditLogEntry selectRoomLog(String username){
        double timestamp = System.currentTimeMillis();
        Action action = Action.SELECT_ROOM;
        AuditLogEntry entry = new AuditLogEntry(username, action, timestamp);
        commands.zadd(AUDIT_LOG, timestamp, entry.toString());
        return entry;
    }

    /*
     *@param username - the username of the admin who displaced a student
     */
    public AuditLogEntry displaceStudentLog(String adminUsername, String displacedStudentUsername){
        double timestamp = System.currentTimeMillis();
        Action action = Action.DISPLACE_STUDENT;
        AuditLogEntry entry = new AuditLogEntry(adminUsername, action, timestamp, displacedStudentUsername);
        commands.zadd(AUDIT_LOG, timestamp, entry.toString());
        return entry;
    }

    public void clearAuditLog(){
        long numLogEntries = commands.zcard(AUDIT_LOG);
        commands.zremrangebyrank(AUDIT_LOG, 0, numLogEntries);
    }

    public void closeRedisConnection(){
        connection.close();
        client.shutdown();
    }
}
