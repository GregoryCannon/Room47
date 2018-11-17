package ServerPackage;

import org.junit.*;

import static junit.framework.TestCase.assertEquals;

public class AuditLogDBTest {
    private static AuditLogDB auditLogDB;

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
        AuditLogEntry entry = auditLogDB.registerLog("John Smith");
        assertEquals(entry.getAction(), AuditLogDB.Action.REGISTER);
        assertEquals(entry.getUsername(), "John Smith");
    }

    @Test
    public void loginLogTest(){
        AuditLogEntry entry = auditLogDB.loginLog("John Smith");
        assertEquals(entry.getAction(), AuditLogDB.Action.LOGIN);
        assertEquals(entry.getUsername(), "John Smith");
    }

    @Test
    public void selectRoomLogTest(){
        AuditLogEntry entry = auditLogDB.selectRoomLog("John Smith");
        assertEquals(entry.getAction(), AuditLogDB.Action.SELECT_ROOM);
        assertEquals(entry.getUsername(), "John Smith");
    }

    @Test
    public void displaceStudentLogTest(){
        AuditLogEntry entry = auditLogDB.displaceStudentLog("John Smith", "Jane Doe");
        assertEquals(entry.getAction(), AuditLogDB.Action.DISPLACE_STUDENT);
        assertEquals(entry.getUsername(), "John Smith");
        assertEquals(entry.getDisplacedStudent(), "Jane Doe");
    }
}

