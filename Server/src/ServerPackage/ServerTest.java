package ServerPackage;

import SSLPackage.Action;
import SSLPackage.ClientPacket;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static SSLPackage.Action.*;
import static SSLPackage.ServerPacket.*;
import static org.junit.Assert.*;

/**
 * Created by Greg on 11/14/18.
 */
public class ServerTest {
    private static Server server;
    private static RedisDB redis;
    private static HashUtil hashUtil;

    @BeforeClass
    public static void init() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        hashUtil = new HashUtil();
        redis = new RedisDB("localhost", 6379);
        setupJohnSmith();
    }

    @Before
    public void createServer() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        server = new Server();
        server.registerUser("testadmin", "adminpass", "01234567");
        server.addAdmin("testadmin");
    }

    private static final String jUsername = "John Smith";
    private static final String jPassword = "passphrase";
    private static void setupJohnSmith() throws UnsupportedEncodingException {
        String salt = "mySalt";
        String registrationTime = "12345";
        String hashedPassword = new String(hashUtil.hashPassword(salt, jPassword), "UTF8");
        redis.createAccount(jUsername, hashedPassword, registrationTime, salt);
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
    public void registrationFailsWithBadStudentId(){
        testAction(REGISTER, "elmer", "fudd12", null, "badID", REGISTRATION_FAILED);
    }

    @Test
    public void userCantReserveFilledRoom(){
        writeDummyData();
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
        writeDummyData();

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

        String oldList = redis.getOccupiedRooms("Walker").trim();
        int oldCount = oldList.split(" ").length;

        testAction(LOG_IN, jUsername, jPassword, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, jUsername, jPassword, "Walker", "208", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        String newList = redis.getOccupiedRooms("Walker").trim();
        int newCount = newList.split(" ").length;

        assertEquals(oldCount + 1, newCount);
    }





    private void testAction(Action a, String username, String password, String dormName, String dormRoomNumber,
                            String expectedResult){
        ClientPacket p = new ClientPacket(a, username, password, dormName, dormRoomNumber);
        assertEquals(expectedResult, server.handle(p).message);
    }

    private static void writeDummyData(){
        try {
            server.registerUser("sam", "passphrase1", "00011122");
            server.registerUser("josh", "passphrase2", "0");
            server.registerUser("patrick", "passphrase3", "000");
            server.registerUser("greg", "passphrase4", "00011133");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
