package ServerPackage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class AuditLogDBTest {
    private static AuditLogDB auditLogDB;
    private static String studentID = "12345";
    private static String studentUsername = "johnsmith";
    private static String adminUsername = "adminUsername";
    private static String dormName = "Walker";
    private static String dormNumber = "112";

    @BeforeClass
    public static void setup(){
        auditLogDB = new AuditLogDB("localhost", 6379);
    }

    @AfterClass
    public static void teardown(){
        auditLogDB.closeRedisConnection();
    }

    @After
    public void teardownAfter(){
        auditLogDB.clearAuditLog();
    }

    @Test
    public void registerLogTest() throws IOException, ClassNotFoundException {
        int initialSize = auditLogDB.getLogsForStudent(studentUsername).size();
        auditLogDB.registerLog(studentID, studentUsername, adminUsername, dormName, dormNumber);
        List<AuditLogEntry> result = auditLogDB.getLogsForStudent(studentUsername);
        assertEquals(initialSize + 1, result.size());

        AuditLogEntry entry = result.get(0);
        assertEquals(entry.getAction(), AuditLogDB.Action.REGISTER);
        assertEquals(entry.getStudentUsername(), "johnsmith");
    }

    @Test
    public void loginLogTest() throws IOException, ClassNotFoundException {
        int initialSize = auditLogDB.getLogsForStudent(studentUsername).size();
        auditLogDB.loginLog(studentID, studentUsername, adminUsername, dormName, dormNumber);
        List<AuditLogEntry> result = auditLogDB.getLogsForStudent(studentUsername);
        assertEquals(initialSize + 1, result.size());

        AuditLogEntry entry = result.get(0);
        assertEquals(entry.getAction(), AuditLogDB.Action.LOGIN);
        assertEquals(entry.getStudentUsername(), "johnsmith");
    }

    @Test
    public void selectRoomLogTest() throws IOException, ClassNotFoundException {
        int initialSize = auditLogDB.getLogsForStudent(studentUsername).size();
        auditLogDB.selectRoomLog(studentID, studentUsername, adminUsername, dormName, dormNumber);
        List<AuditLogEntry> result = auditLogDB.getLogsForStudent(studentUsername);
        assertEquals(initialSize + 1, result.size());

        AuditLogEntry entry = result.get(0);

        assertEquals(entry.getAction(), AuditLogDB.Action.SELECT_ROOM);
        assertEquals(entry.getStudentUsername(), "johnsmith");
    }

    @Test
    public void displaceStudentLogTest() throws IOException, ClassNotFoundException {
        int initialSize = auditLogDB.getLogsForStudent(studentUsername).size();
        auditLogDB.displaceStudentLog(studentID, studentUsername, adminUsername, dormName, dormNumber);
        List<AuditLogEntry> result = auditLogDB.getLogsForStudent(studentUsername);
        assertEquals(initialSize + 1, result.size());

        AuditLogEntry entry = result.get(0);
        assertEquals(entry.getAction(), AuditLogDB.Action.DISPLACE_STUDENT);
        assertEquals(entry.getStudentUsername(), "johnsmith");
    }

    @Test
    public void placeStudentLogTest() throws IOException, ClassNotFoundException {
        int initialSize = auditLogDB.getLogsForStudent(studentUsername).size();
        auditLogDB.placeStudentLog(studentID, studentUsername, adminUsername, dormName, dormNumber);
        List<AuditLogEntry> result = auditLogDB.getLogsForStudent(studentUsername);
        assertEquals(initialSize + 1, result.size());

        AuditLogEntry entry = result.get(0);

        assertEquals(entry.getAction(), AuditLogDB.Action.PLACE_STUDENT);
        assertEquals(entry.getStudentUsername(), "johnsmith");
    }
}

