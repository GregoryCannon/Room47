package ServerPackage;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Created by Greg on 12/13/18.
 */
public class EncryptionManager {
    private String dbEncryptionKey;
    private String initVector;

    public EncryptionManager(String dbEncryptionKey, String initVector){
        this.initVector = initVector;
        this.dbEncryptionKey = dbEncryptionKey;
    }

    public String AESEncrypt(String plaintext){
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(dbEncryptionKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String AESDecrypt(String ciphertext){
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(dbEncryptionKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(ciphertext));

            return new String(plaintext);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
