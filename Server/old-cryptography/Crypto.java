package cryptography;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.RandomStringUtils;

public class Crypto {

	/*
	 * Signing
	 */
	public static byte[] signEstablishPacket(String name, long time, byte[] enc){
		try{
			String encString = new String(enc, "UTF-8");
			String params = name + time + encString;
			byte[] p = params.getBytes();
			byte[] signature = generateSignature(cryptography.Keys.getPrivateKeyAlice(), p);
			return signature;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] generateSignature(PrivateKey dsaPrivate, byte[] input) throws GeneralSecurityException {
		Signature signature = Signature.getInstance("SHA384withRSA", "BC");
		signature.initSign(dsaPrivate);
		signature.update(input);
		return signature.sign();
	}
	
	public static byte[] calculateHmac(SecretKey key, byte[] data) throws GeneralSecurityException {
		Mac hmac = Mac.getInstance("HMacSHA512", "BC"); hmac.init(key);
		return hmac.doFinal(data);
	}
	
	/*
	 * RSA Encryption
	 */
	
	public static byte[] encryptSessionKey(PublicKey rsaPublic, SecretKey secret) throws NoSuchAlgorithmException,
	NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		Cipher c = Cipher.getInstance("RSA/NONE/OAEPPadding", "BC");
		c.init(Cipher.WRAP_MODE, rsaPublic);
		return c.wrap(secret);
	}
	
	public static Key decryptSessionKey(PrivateKey rsaPrivate, byte[] wrappedKey)
			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
		Cipher c = Cipher.getInstance("RSA/NONE/OAEPPadding", "BC");
		c.init(Cipher.UNWRAP_MODE, rsaPrivate);
		return c.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
	}
	
	/*
	 * Symmetric Encryption
	 */

	public static byte[][] cbcEncrypt(SecretKey key, byte[] data) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		String randIV = RandomStringUtils.random(16, 0, 16, true, true, "0123456789abcdef".toCharArray());
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(randIV.getBytes()));
		return new byte[][] { cipher.getIV(), cipher.doFinal(data) };
	}

	public static byte[] cbcDecrypt(SecretKey key, byte[] iv, byte[] cipherText) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(cipherText);
	}
	
	/*
	 * Generating keys
	 */

	public static SecretKey generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "BC");
		keyGenerator.init(128);
		return keyGenerator.generateKey();
	}

}
