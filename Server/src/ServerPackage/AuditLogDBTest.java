package ServerPackage;

import org.junit.*;

import static junit.framework.TestCase.assertEquals;

public class AuditLogDBTest {
    private static AuditLogDB auditLogDB;
    private static String studentID = "12345";
    private static String studentUsername = "studentUsername";
    private static String adminUsername = "adminUsername";
    private static String displacedStudent = "";
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
    public void registerLogTest(){
        AuditLogEntry entry = auditLogDB.registerLog(studentID, studentUsername, adminUsername, displacedStudent, dormName, dormNumber);
        assertEquals(entry.getAction(), AuditLogDB.Action.REGISTER);
        assertEquals(entry.getStudentUsername(), "studentUsername");
    }

    @Test
    public void loginLogTest(){
        AuditLogEntry entry = auditLogDB.loginLog(studentID, studentUsername, adminUsername, displacedStudent, dormName, dormNumber);
        assertEquals(entry.getAction(), AuditLogDB.Action.LOGIN);
        assertEquals(entry.getStudentUsername(), "studentUsername");
    }

    @Test
    public void selectRoomLogTest(){
        AuditLogEntry entry = auditLogDB.selectRoomLog(studentID, studentUsername, adminUsername, displacedStudent, dormName, dormNumber);
        assertEquals(entry.getAction(), AuditLogDB.Action.SELECT_ROOM);
        assertEquals(entry.getStudentUsername(), "studentUsername");
    }

    @Test
    public void displaceStudentLogTest(){
        displacedStudent = "John Smith";
        AuditLogEntry entry = auditLogDB.displaceStudentLog(studentID, studentUsername, adminUsername, displacedStudent, dormName, dormNumber);
        assertEquals(entry.getAction(), AuditLogDB.Action.DISPLACE_STUDENT);
        assertEquals(entry.getStudentUsername(), "studentUsername");
        assertEquals(entry.getDisplacedStudent(), "John Smith");
    }

    @Test
    public void placeStudentLogTest(){
        displacedStudent = "John Smith";
        AuditLogEntry entry = auditLogDB.placeStudentLog(studentID, studentUsername, adminUsername, displacedStudent, dormName, dormNumber);
        assertEquals(entry.getAction(), AuditLogDB.Action.PLACE_STUDENT);
        assertEquals(entry.getStudentUsername(), "studentUsername");
        assertEquals(entry.getDisplacedStudent(), "John Smith");
    }
}

