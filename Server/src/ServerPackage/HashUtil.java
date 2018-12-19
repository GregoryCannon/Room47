package ServerPackage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    private MessageDigest digest;

    public HashUtil() throws NoSuchAlgorithmException {
        digest = MessageDigest.getInstance("SHA-256");
    }

//    public MessageDigest getHashFunction(){
//        return digest;
//    }
//
//    public void setHashFunction(String hashFunction) throws NoSuchAlgorithmException {
//        digest = MessageDigest.getInstance(hashFunction);
//    }

    public byte[] hashPassword(String salt, String password){
        String input = salt + password;
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }
}
