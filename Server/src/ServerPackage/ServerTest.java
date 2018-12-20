package ServerPackage;

import SSLPackage.Action;
import SSLPackage.ClientPacket;
import SSLPackage.ServerPacket;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import static SSLPackage.Action.*;
import static SSLPackage.ServerPacket.*;
import static ServerPackage.ServerTestAccountData.*;
import static org.junit.Assert.*;

/**
 * Created by Greg on 11/14/18.
 */
public class ServerTest {
    private static Server server;
    private static RedisDB redis;
    private static StudentDataManager studentDataManager;
    private static HashUtil hashUtil;
    private static EmailManager emailManager;
    private static AuditLogDB auditLogDB;

    @BeforeClass
    public static void init() throws Exception {
        // Initialize dependencies
        EncryptionManager encryptionManager = new EncryptionManager(Server.dbEncryptionKey, Server.initVector);
        redis = new RedisDB("localhost", 6379, encryptionManager);
        studentDataManager = new StudentDataManager(redis, encryptionManager);
        hashUtil = new HashUtil();
        emailManager = new EmailManager();
        auditLogDB = new AuditLogDB("localhost", 6379);
    }

    @Before
    public void createFreshServer() throws Exception {
        auditLogDB.clearAuditLog();
        redis.clearRedisDB();
        server = new Server(redis, studentDataManager, hashUtil, emailManager, auditLogDB);
        setupTestData();
    }

    /*
        LOGIN
     */

    @Test
    public void canLogIn() throws UnsupportedEncodingException{
        testAction(LOG_IN, JS_USERNAME, JS_PASS, null, null, LOGIN_SUCCESSFUL);
    }

    @Test
    public void loginFailsWithWrongPassword() throws UnsupportedEncodingException {
        testAction(LOG_IN, JS_USERNAME, "this password is wrong", null, null, LOGIN_FAILED);
    }

    @Test
    public void loginFailsWithWrongUsername() throws UnsupportedEncodingException {
        testAction(LOG_IN, "Jane Doe", JS_PASS, null, null, LOGIN_FAILED);
    }

    @Test
    public void cannotLogOutWhenNotLoggedIn() {
        testAction(LOG_OUT, null, null, null, null, NOT_LOGGED_IN);
    }

    @Test
    public void canLogOutAfterLoggingIn(){
        testAction(LOG_IN, JS_USERNAME, JS_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    /*
        REGISTRATION
     */

    @Test
    public void canRegister(){
        testAction(REGISTER, "bambi", "DeerTr@x9", null, "33333333", REGISTRATION_SUCCESSFUL);
    }

    @Test
    public void cannotRegisterWithInvalidPassword(){
        testAction(REGISTER, "bambi", "abcdefg", null, "33333333", REGISTRATION_FAILED_PASSWORD);
        testAction(REGISTER, "bambi", "ABCDEFG", null, "33333333", REGISTRATION_FAILED_PASSWORD);
        testAction(REGISTER, "bambi", "1237593", null, "33333333", REGISTRATION_FAILED_PASSWORD);
        testAction(REGISTER, "bambi", "$*@(&@@", null, "33333333", REGISTRATION_FAILED_PASSWORD);
        testAction(REGISTER, "bambi", "DeerTrax9", null, "33333333", REGISTRATION_FAILED_PASSWORD);
        testAction(REGISTER, "bambi", "DeerTr@x", null, "33333333", REGISTRATION_FAILED_PASSWORD);
        testAction(REGISTER, "bambi", "deertr@x9", null, "33333333", REGISTRATION_FAILED_PASSWORD);
        testAction(REGISTER, "bambi", "DEERTR@X9", null, "33333333", REGISTRATION_FAILED_PASSWORD);
    }

    @Test
    public void cannotRegisterTwiceWithSameStudentId(){
        testAction(REGISTER, "elmer", "Fudd12#$%", null, "00001111", REGISTRATION_SUCCESSFUL);
        testAction(REGISTER, "donald", "Fudd12#$%", null, "00001111", REGISTRATION_FAILED_STUDENT_ID);
    }

    @Test
    public void cannotRegisterTwiceWithSameUsername(){
        testAction(REGISTER, "elmer", "Fudd12#$%", null, "00001111", REGISTRATION_SUCCESSFUL);
        testAction(REGISTER, "elmer", "Fudd12newpass$", null, "77777777", REGISTRATION_FAILED_USERNAME);
    }

    @Test
    public void registrationFailsWithBadStudentId(){
        testAction(REGISTER, "elmer", "fudd12", null, "badID", REGISTRATION_FAILED_STUDENT_ID);
    }

    @Test
    public void cannotReserveFilledRoom(){
        redis.clearRoom("Walker", "208");
        testAction(LOG_IN, GREG_USERNAME, GREG_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Walker", "208", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        testAction(LOG_IN, PATRICK_USERNAME, PATRICK_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Walker", "208", RESERVE_FAILED);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    /*
        REQUESTING ROOMS
     */

    @Test
    public void canRequestRoom(){
        redis.clearRoom("Clark I", "117");
        testAction(LOG_IN, JS_USERNAME, JS_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Clark I", "117", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void cannotRequestRoomBeforeTheirTime(){
        redis.clearRoom("Clark I", "117");
        testAction(LOG_IN, WAITING_USERNAME, WAITING_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Clark I", "117", RESERVE_FAILED);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void cannotRequestRoomWhenNotLoggedIn(){
        testAction(REQUEST_ROOM, null, null, "Clark I", "117", NOT_LOGGED_IN);
    }

    /*
        ADMIN FUNCTIONS
     */

    @Test
    public void cannotUseAdminFunctionsWhenNotLoggedIn(){
        testAction(ADMIN_REMOVE_STUDENT, null, null, "Clark I", "117", ADMIN_UNAUTHORIZED);
        testAction(ADMIN_PLACE_STUDENT, "sam", null, "Clark I", "117", ADMIN_UNAUTHORIZED);
    }

    @Test
    public void onlyAdminsCanUseAdminFunctions(){
        testAction(LOG_IN, GREG_USERNAME, GREG_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(ADMIN_REMOVE_STUDENT, null, null, "Clark I", "117", ADMIN_UNAUTHORIZED);
        testAction(ADMIN_PLACE_STUDENT, SAM_USERNAME, null, "Clark I", "117", ADMIN_UNAUTHORIZED);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        testAction(LOG_IN, ADMIN_USERNAME, ADMIN_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(ADMIN_REMOVE_STUDENT, null, null, "Clark I", "117", REMOVE_STUDENT_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, SAM_USERNAME, null, "Clark I", "117", PLACE_STUDENT_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void adminCanPlaceStudents(){
        testAction(LOG_IN, ADMIN_USERNAME, ADMIN_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, SAM_USERNAME, null, "Clark V", "117", PLACE_STUDENT_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, JOSH_USERNAME, null, "Clark I", "117", PLACE_STUDENT_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, GREG_USERNAME, null, "Walker", "204", PLACE_STUDENT_SUCCESSFUL);
        testAction(ADMIN_PLACE_STUDENT, PATRICK_USERNAME, null, "Walker", "208", PLACE_STUDENT_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        assertEquals(redis.getDormName(SAM_USERNAME), "Clark V");
        assertEquals(redis.getDormRoomNumber(SAM_USERNAME), "117");
        assertEquals(redis.getDormName(JOSH_USERNAME), "Clark I");
        assertEquals(redis.getDormRoomNumber(JOSH_USERNAME), "117");
        assertEquals(redis.getDormName(GREG_USERNAME), "Walker");
        assertEquals(redis.getDormRoomNumber(GREG_USERNAME), "204");
        assertEquals(redis.getDormName(PATRICK_USERNAME), "Walker");
        assertEquals(redis.getDormRoomNumber(PATRICK_USERNAME), "208");
    }

    /*
        GETTING INFO
     */

    @Test
    public void canGetOccupiedRooms(){
        redis.setDormName(JS_USERNAME, "-1");
        redis.setDormRoomNumber(JS_USERNAME, "-1");
        redis.clearRoom("Walker", "208");
        redis.clearRoom("Walker", "204");

        Set<String> oldList = responseFromTestingAction(GET_ROOMS, null, null, "Walker", null).occupiedRooms;
        int oldCount = oldList.size();

        testAction(LOG_IN, JS_USERNAME, JS_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Walker", "208", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        testAction(LOG_IN, ServerTestAccountData.GREG_USERNAME, GREG_PASS, null, null, LOGIN_SUCCESSFUL);
        testAction(REQUEST_ROOM, null, null, "Walker", "204", RESERVE_SUCCESSFUL);
        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);

        Set<String> newList = responseFromTestingAction(GET_ROOMS, null, null, "Walker", null).occupiedRooms;
        int newCount = newList.size();

        assertEquals(oldCount + 2, newCount);
    }

    @Test
    public void canGetInfo(){
        testAction(LOG_IN, JS_USERNAME, JS_PASS, null, null, LOGIN_SUCCESSFUL);

        // Manually validate the response
        String response = responseFromTestingAction(GET_INFO, null, null, null, null).message;
        assertNotEquals(response, GET_INFO_FAILED);
        assertTrue(response.length() > 10);

        testAction(LOG_OUT, null, null, null, null, LOGOUT_SUCCESSFUL);
    }

    @Test
    public void cannotGetInfoWhenNotLoggedIn(){
        testAction(GET_INFO, null, null, null, null, NOT_LOGGED_IN);
    }

    @Test
    public void canGetOccupiedRoomsWhenNotLoggedIn(){
        // Manually validate the response
        String response = responseFromTestingAction(GET_ROOMS, null, null, null, null).message;
        assertNotEquals(response, NOT_LOGGED_IN);
    }

    /*
        RATE LIMITING
     */

    @Test
    public void doesCapPacketsBeforeLogin(){
        for (int i = 0; i < Server.RATE_LIMIT; i++){
            testAction(LOG_IN, JS_USERNAME, "notJohnsPassword", null, null, LOGIN_FAILED);
        }
        testAction(LOG_IN, JS_USERNAME, "stillNotHisPassword", null, null, RATE_LIMIT_REACHED);
    }

    @Test
    public void doesCapPacketsAfterLogin(){
        testAction(LOG_IN, JS_USERNAME, JS_PASS, null, null, LOGIN_SUCCESSFUL);
        for (int i = 0; i < Server.RATE_LIMIT - 1; i++){
            testAction(ADMIN_PLACE_STUDENT, GREG_USERNAME, null, "Smiley", "303", ADMIN_UNAUTHORIZED);
        }
        testAction(ADMIN_PLACE_STUDENT, GREG_USERNAME, null, "Smiley", "303", RATE_LIMIT_REACHED);
    }


    /*
        PASSWORD RESETTING
     */

    @Test
    public void canResetPasswordFromTemp(){
        final String newPassword = "Newpass1!";
        ServerPacket response = responseFromTestingAction(REQUEST_TEMP_PASSWORD, GREG_USERNAME, null, null, null);
        assertEquals(REQUEST_TEMP_PASSWORD_SUCCESSFUL, response.message);
        assertTrue(response.tempPassword != null && response.tempPassword.length() > 0);
        testAction(RESET_PASSWORD, GREG_USERNAME, newPassword, response.tempPassword, null, RESET_PASSWORD_SUCCESSFUL);
        testAction(LOG_IN, GREG_USERNAME, newPassword, null, null, LOGIN_SUCCESSFUL);
    }

//    @Test
//    public void canSendRealTempPasswordToGreg() {
//        testAction(REQUEST_TEMP_PASSWORD, GREG_USERNAME, null, null, null, REQUEST_TEMP_PASSWORD_SUCCESSFUL);
//    }



    /*
        HELPER METHODS
     */

    static void testAction(Action a, String username, String password, String dormName, String dormRoomNumber,
                            String expectedResult){
        ClientPacket p = new ClientPacket(a, username, password, dormName, dormRoomNumber);
        assertEquals(expectedResult, server.handle(p).message);
    }

    static ServerPacket responseFromTestingAction(Action a, String username, String password, String dormName,
                                                   String dormRoomNumber){
        ClientPacket p = new ClientPacket(a, username, password, dormName, dormRoomNumber);
        return server.handle(p);
    }

    private static void setupTestData() throws Exception{
        try {
            boolean success = server.actor.registerUser(SAM_USERNAME, SAM_PASS, SAM_ID, true);
            success = success & server.actor.registerUser(JOSH_USERNAME, JOSH_PASS, JOSH_ID, true);
            success = success & server.actor.registerUser(PATRICK_USERNAME, PATRICK_PASS, PATRICK_ID, true);
            success = success & server.actor.registerUser(GREG_USERNAME, GREG_PASS, GREG_ID, true);

            success = success & server.actor.registerUser(JS_USERNAME, JS_PASS, JS_ID, true);
            success = success & server.actor.registerUser(WAITING_USERNAME, WAITING_PASS, WAITING_ID, false);

            success = success & server.actor.registerUser(ADMIN_USERNAME, ADMIN_PASS, ADMIN_ID, true);
            server.actor.addAdmin(ADMIN_USERNAME);
            if (!success) throw new Exception("Failed to initialize test data.");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
