import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class RedisDBTest {
    private static RedisDB redis;

    @BeforeClass
    public static void setUp(){
      redis = new RedisDB("localhost", 6379);
    }

    @AfterClass
    public static void tearDown(){
        redis.closeRedisConnection();
    }

    @Test
    public void testCreateAccount(){
        redis.createAccount("Sam Gearou", "examplePassword");
        assertEquals(redis.getPassword("Sam Gearou"), "examplePassword");
    }

    @Test
    public void testRoomDrawNumber(){
        redis.setRoomDrawNumber("Sam Gearou", "1234");
        assertEquals(redis.getRoomDrawNumber("Sam Gearou"), "1234");
    }

    @Test
    public void testDormRoom(){
        redis.setDormRoom("Sam Gearou", "Clark I");
        assertEquals(redis.getDormRoom("Sam Gearou"), "Clark I");
    }

    @Test
    public void testDormRoomNumber(){
        redis.setDormRoomNumber("Sam Gearou", "436");
        assertEquals(redis.getDormRoomNumber("Sam Gearou"), "436");
    }
}
