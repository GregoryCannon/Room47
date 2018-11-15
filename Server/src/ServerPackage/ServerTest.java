package ServerPackage;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Created by Greg on 11/14/18.
 */
public class ServerTest {
    private static Server server;
    private static RedisDB redis;
    private static HashUtil hashUtil;

    @BeforeClass
    public static void init() throws NoSuchAlgorithmException {
        hashUtil = new HashUtil();
        redis = new RedisDB("localhost", 6379, hashUtil);
        server = new Server();
        writeDummyData();
    }

    @Test
    public void simpleRoomAssignment(){
        server.requestRoom("Clark V", "117", "sam");
        server.requestRoom("Clark I", "117", "josh");
        server.requestRoom("Walker", "204", "greg");
        server.requestRoom("Walker", "208", "patrick");

        assertEquals(redis.getDormRoom("sam"), "Clark V");
        assertEquals(redis.getDormRoomNumber("sam"), "117");
        assertEquals(redis.getDormRoom("josh"), "Clark I");
        assertEquals(redis.getDormRoomNumber("josh"), "117");
        assertEquals(redis.getDormRoom("greg"), "Walker");
        assertEquals(redis.getDormRoomNumber("greg"), "204");
        assertEquals(redis.getDormRoom("patrick"), "Walker");
        assertEquals(redis.getDormRoomNumber("patrick"), "208");
    }

    @Test
    public void canLogIn() throws UnsupportedEncodingException {
        String username = "John Smith";
        String password = "passphrase";
        String salt = "mySalt";
        String registrationTime = "12345";
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");
        redis.createAccount(username, hashedPassword, registrationTime, salt);
        assertTrue(server.logIn(username, password));
    }

    @Test
    public void loginFailsWithWrongPassword() throws UnsupportedEncodingException {
        String username = "John Smith";
        String password = "passphrase";
        String salt = "mySalt";
        String registrationTime = "12345";
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");
        redis.createAccount(username, hashedPassword, registrationTime, salt);
        assertFalse(server.logIn(username, "incorrect"));
    }

    @Test
    public void loginFailsWithWrongUsername() throws UnsupportedEncodingException {
        String username = "John Smith";
        String password = "passphrase";
        String salt = "mySalt";
        String registrationTime = "12345";
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");
        redis.createAccount(username, hashedPassword, registrationTime, salt);
        assertFalse(server.logIn("Jane Doe", hashedPassword));
    }

    private static void writeDummyData(){
        try {
            server.registerUser("sam", "passphrase1");
            server.registerUser("josh", "passphrase2");
            server.registerUser("patrick", "passphrase3");
            server.registerUser("greg", "passphrase4");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
