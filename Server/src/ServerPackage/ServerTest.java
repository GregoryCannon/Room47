package ServerPackage;

import SSLPackage.Action;
import SSLPackage.ClientPacket;
import org.junit.Before;
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
    public static void init() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        hashUtil = new HashUtil();
        redis = new RedisDB("localhost", 6379, hashUtil);
        setupJohnSmith();
    }

    @Before
    public void createServer() throws NoSuchAlgorithmException {
        server = new Server();
    }

    @Test
    public void simpleRoomAssignment(){
        writeDummyData();

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

    private static final String jUsername = "John Smith";
    private static final String jPassword = "passphrase";
    private static void setupJohnSmith() throws UnsupportedEncodingException {
        String salt = "mySalt";
        String registrationTime = "12345";
        String hashedPassword = new String(hashUtil.hashPassword(salt, jPassword), "UTF8");
        redis.createAccount(jUsername, hashedPassword, registrationTime, salt);
    }

    @Test
    public void canLogIn() throws UnsupportedEncodingException {
        assertTrue(server.logIn(jUsername, jPassword));
    }

    @Test
    public void canLogInWithPacket() throws UnsupportedEncodingException{
        ClientPacket p = new ClientPacket(Action.LOG_IN, jUsername, jPassword, null, null, null);
        assertEquals(server.handle(p).message, "Login successful");
    }

    @Test
    public void loginFailsWithWrongPassword() throws UnsupportedEncodingException {
        assertFalse(server.logIn(jUsername, "incorrect password"));
    }

    @Test
    public void loginFailsWithWrongPasswordWithPacket() throws UnsupportedEncodingException {
        ClientPacket p = new ClientPacket(Action.LOG_IN, jUsername, "incorrect password", null, null, null);
        assertEquals(server.handle(p).message, "Login failed");
    }

    @Test
    public void loginFailsWithWrongUsername() throws UnsupportedEncodingException {
        assertFalse(server.logIn("Jane Doe", jPassword));
    }

    @Test
    public void loginFailsWithWrongUsernameWithPacket() throws UnsupportedEncodingException {
        ClientPacket p = new ClientPacket(Action.LOG_IN, "Jane Doe", jPassword, null, null, null);
        assertEquals(server.handle(p).message, "Login failed");
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
