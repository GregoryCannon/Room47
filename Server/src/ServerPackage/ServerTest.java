package ServerPackage;

import SSLPackage.Action;
import SSLPackage.ClientPacket;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static SSLPackage.Action.*;
import static SSLPackage.ServerPacket.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Greg on 11/14/18.
 */
public class ServerTest {
    private static Server server;
    private static RedisDB redis;

    // Username and password for test user John Smith
    private static final String jUsername = "johnsmith";
    private static final String jPassword = "passphrase";

    @BeforeClass
    public static void init() throws Exception {
        redis = new RedisDB("localhost", 6379);
    }

    @Before
    public void createServer() throws Exception {
        redis.clearRedisDB();
        server = new Server();
        setupTestData();
    }

    @Test
    public void canLogIn() throws UnsupportedEncodingException{
        testAction(LOG_IN, jUsername, jPassword, null, null, LOGIN_SUCCESSFUL);
    }

    @Test
    public void loginFailsWithWrongPassword() throws UnsupportedEncodingException {
        testAction(LOG_IN, jUsername, "this password is wrong", null, null, LOGIN_FAILED);
    }

    @Test
    public void loginFailsWithWrongUsername() throws UnsupportedEncodingException {
        testAction(LOG_IN, "Jane Doe", jPassword, null, null, LOGIN_FAILED);
    }

    @Test
    public void cantLogOutWhenNotLoggedIn() {
        testAction(LOG_OUT, null, null, null, null, NOT_LOGGED_IN);
    }

    @Test
    public void canLogOutAfterLoggingIn(){
        testAction(LOG_IN, jUsername, jPassword, null, null, LOGIN_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void userCanRegister(){
        testAction(REGISTER, "elmer", "fudd12", null, "00001111", REGISTRATION_SUCCESSFUL);
    }

    @Test
    public void userCannotRegisterTwice(){
        testAction(REGISTER, "elmer", "fudd12", null, "00001111", REGISTRATION_SUCCESSFUL);
        testAction(REGISTER, "elmer", "fudd12", null, "00001111", REGISTRATION_FAILED);
    }

    @Test
    public void registrationFailsWithBadStudentId(){
        testAction(REGISTER, "elmer", "fudd12", null, "badID", REGISTRATION_FAILED);
    }

    @Test
    public void userCantReserveFilledRoom(){
        redis.clearRoom("Walker", "208");
        testAction(LOG_IN, "greg", "passphrase4", null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Walker", "208", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        testAction(LOG_IN, "patrick", "passphrase3", null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Walker", "208", RESERVE_FAILED);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void userCanRequestRoom(){
        redis.clearRoom("Clark I", "117");
        testAction(LOG_IN, jUsername, jPassword, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Clark I", "117", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void userCantRequestRoomBeforeTheirTime(){
        redis.clearRoom("Clark I", "117");
        testAction(LOG_IN, "stillwaiting", "waiting", null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Clark I", "117", RESERVE_FAILED);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void adminFunctionsDontWorkWhenNotAuthenticated(){
        testAction(ADMIN_REMOVE_STUDENT, null, null, "Clark I", "117", ADMIN_UNAUTHORIZED);

        testAction(ADMIN_PLACE_STUDENT, "sam", null, "Clark I", "117", ADMIN_UNAUTHORIZED);
    }

    @Test
    public void onlyAdminsCanUseAdminFunctions(){
        testAction(LOG_IN, "greg", "passphrase4", null, null, LOGIN_SUCCESSFUL);
        testAction(ADMIN_REMOVE_STUDENT, null, null, "Clark I", "117", ADMIN_UNAUTHORIZED);
        testAction(ADMIN_PLACE_STUDENT, "sam", null, "Clark I", "117", ADMIN_UNAUTHORIZED);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        testAction(LOG_IN, "testadmin", "adminpass", null, null, LOGIN_SUCCESSFUL);
        testAction(ADMIN_REMOVE_STUDENT, null, null, "Clark I", "117", REMOVE_STUDENT_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, "sam", null, "Clark I", "117", PLACE_STUDENT_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void adminCanPlaceStudents(){
        testAction(LOG_IN, "testadmin", "adminpass", null, null, LOGIN_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, "sam", null, "Clark V", "117", PLACE_STUDENT_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, "josh", null, "Clark I", "117", PLACE_STUDENT_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, "greg", null, "Walker", "204", PLACE_STUDENT_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, "patrick", null, "Walker", "208", PLACE_STUDENT_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        assertEquals(redis.getDormName("sam"), "Clark V");
        assertEquals(redis.getDormRoomNumber("sam"), "117");
        assertEquals(redis.getDormName("josh"), "Clark I");
        assertEquals(redis.getDormRoomNumber("josh"), "117");
        assertEquals(redis.getDormName("greg"), "Walker");
        assertEquals(redis.getDormRoomNumber("greg"), "204");
        assertEquals(redis.getDormName("patrick"), "Walker");
        assertEquals(redis.getDormRoomNumber("patrick"), "208");
    }

    @Test
    public void canGetOccupiedRooms(){
        redis.setDormName(jUsername, "-1");
        redis.setDormRoomNumber(jUsername, "-1");
        redis.clearRoom("Walker", "208");
        redis.clearRoom("Walker", "204");

        String oldList = redis.getOccupiedRooms("Walker").trim();
        int oldCount = Math.min(oldList.split(" ").length, oldList.length());

        testAction(LOG_IN, jUsername, jPassword, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Walker", "208", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        testAction(LOG_IN, "greg", "passphrase4", null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Walker", "204", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        String newList = redis.getOccupiedRooms("Walker").trim();
        int newCount = newList.split(" ").length;

        assertEquals(oldCount + 2, newCount);
    }

    @Test
    public void canGetInfo(){
        testAction(LOG_IN, jUsername, jPassword, null, null, LOGIN_SUCCESSFUL);
        ClientPacket p = new ClientPacket(GET_INFO, null, null, null, null);
        String response = server.handle(p).message;
        assertTrue(response.length() > 10);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }



    private void testAction(Action a, String username, String password, String dormName, String dormRoomNumber,
                            String expectedResult){
        ClientPacket p = new ClientPacket(a, username, password, dormName, dormRoomNumber);
        assertEquals(expectedResult, server.handle(p).message);
    }

    private static void setupTestData() throws Exception{
        try {
            boolean success = server.registerUser("sam", "passphrase1", "00011122", true);
            success = success & server.registerUser("josh", "passphrase2", "00011133", true);
            success = success & server.registerUser("patrick", "passphrase3", "00011144", true);
            success = success & server.registerUser("greg", "passphrase4", "00011155", true);

            success = success & server.registerUser(jUsername, jPassword, "12121212", true);
            success = success & server.registerUser("stillwaiting", "waiting", "87878787", false);

            success = success & server.registerUser("testadmin", "adminpass", "01234567", true);
            server.addAdmin("testadmin");
            if (!success) throw new Exception("Failed to initialize test data.");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
