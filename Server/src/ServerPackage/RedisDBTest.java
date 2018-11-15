package ServerPackage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedisDBTest {
    private static RedisDB redis;
    private static HashUtil hashUtil;

    @BeforeClass
    public static void setUp() throws NoSuchAlgorithmException {
        hashUtil = new HashUtil();
        redis = new RedisDB("localhost", 6379, hashUtil);
    }

    @AfterClass
    public static void tearDown() {
        redis.closeRedisConnection();
    }

    @Test
    public void testRoomDrawNumber() {
        String username = "Sam Gearou";
        String roomDrawNumber = "1234";
        redis.setRoomDrawNumber(username, roomDrawNumber);
        assertEquals(redis.getRoomDrawNumber(username), "1234");
    }

    @Test
    public void testDormRoom() {
        String username = "Sam Gearou";
        String dormRoom = "Clark I";
        redis.setDormRoom(username, dormRoom);
        assertEquals(redis.getDormRoom(username), "Clark I");
    }

    @Test
    public void testDormRoomNumber() {
        String username = "Sam Gearou";
        String dormRoomNumber = "436";
        redis.setDormRoomNumber(username, dormRoomNumber);
        assertEquals(redis.getDormRoomNumber(username), "436");
    }

    @Test
    public void testCreateAccountWithSalt() throws UnsupportedEncodingException {
        String username = "John Smith";
        String password = "passphrase";
        String salt = "123456";
        String registrationTime = "98765";
        redis.createAccount(username, password, registrationTime, salt);
        assertEquals(redis.getSalt(username), "123456");
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");
        assertEquals(redis.getPassword(username), hashedPassword);
    }

    @Test
    public void testRegistrationTime() {
        String username = "John Smith";
        String registrationTime = "98765";
        redis.setRegistrationTime(username, registrationTime);
        assertEquals(redis.getRegistrationTime(username), "98765");
    }
    
    @Test
    public void testIsAuthenticated() throws UnsupportedEncodingException {
        String username = "John Smith";
        String password = "passphrase";
        String salt = "mySalt";
        String registrationTime = "12345";
        redis.createAccount(username, password, registrationTime, salt);
        assertTrue(redis.isAuthenticated(username, salt, password));
    }
}
