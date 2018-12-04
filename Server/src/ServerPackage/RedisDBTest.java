package ServerPackage;

import org.junit.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static junit.framework.TestCase.assertEquals;

public class RedisDBTest {
    private static RedisDB redis;
    private static HashUtil hashUtil;

    @BeforeClass
    public static void setUp() throws NoSuchAlgorithmException {
        hashUtil = new HashUtil();
        redis = new RedisDB("localhost", 6379);
    }

    @AfterClass
    public static void tearDown() {
        redis.closeRedisConnection();
    }

    @After
    public void tearDownAfter(){
        redis.clearRedisDB();
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
        redis.setDormName(username, dormRoom);
        assertEquals(redis.getDormName(username), "Clark I");
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
        String fullName = "Johnathan Smith";
        String studentId = "77779999";
        String salt = "123456";
        String registrationTime = "98765";
        String hashedPassword = new String(hashUtil.hashPassword(salt, password), "UTF8");

        redis.createAccount(username, hashedPassword, registrationTime, salt, fullName, studentId);
        assertEquals(redis.getSalt(username), "123456");
        assertEquals(redis.getHashedPassword(username), hashedPassword);
    }

    @Test
    public void testRegistrationTime() {
        String username = "John Smith";
        String registrationTime = "98765";
        redis.setRegistrationTime(username, registrationTime);
        assertEquals(redis.getRegistrationTime(username), registrationTime);
    }

    @Test
    public void testUserID(){
        String username = "John Smith";
        String userID = "54321";
        redis.setUserID(username, userID);
        assertEquals(redis.getUserID(username), "54321");
    }

    @Test
    public void testFullName(){
        String username = "adumbledore";
        String fullName = "Albus Percival Wulfric Brian Dumbledore";
        redis.setFullName(username, fullName);
        assertEquals(redis.getFullName(username), "Albus Percival Wulfric Brian Dumbledore");
    }

    @Test
    public void testRateLimit(){
        String username = "adumbledore";
        int rateLimit = 1000;
        redis.setPacketCount(username, rateLimit);
        assertEquals(redis.getPacketCount(username), 1000);
    }

    @Test
    public void testGetOccupantOfRoom() throws UnsupportedEncodingException {
        String username = "user";
        String hashedPassword = "qwerty";
        String registrationTime = "1234";
        String fullName = "fully namey";
        String studentId = "44443333";
        String salt = "321";
        for(int i = 0; i<10; i++){
            redis.createAccount(username + i, hashedPassword + i,
                    registrationTime + i, salt + i, fullName, studentId);
            if(i % 2 == 0){
                redis.setDormName(username + i, "Walker");
            }
            else{
                redis.setDormName(username + i, "Clark I");
            }
            redis.setDormRoomNumber(username + i, i + "");
        }
        String getUser = redis.getOccupantOfRoom("Clark I", "3");
        assertEquals(getUser, "user3");
        assertEquals(redis.getUsers().size(), 10);
    }
}
