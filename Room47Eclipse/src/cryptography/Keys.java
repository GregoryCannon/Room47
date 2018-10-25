package cryptography;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;

public class Keys {
	private static PublicKey pubA;
	private static PublicKey pubB;
	private static PrivateKey priA;
	private static PrivateKey priB;
	private static SecretKey secretKey;
	
	public static PublicKey getPublicKeyAlice() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		Path path = Paths.get("AlicePub.txt");
		byte[] bytes = Files.readAllBytes(path);
		
		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		pubA = kf.generatePublic(ks);
		return pubA;
	}
	
	public static PublicKey getPublicKeyBob() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		Path path = Paths.get("BobPub.txt");
		byte[] bytes = Files.readAllBytes(path);
		
		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		pubB = kf.generatePublic(ks);
		return pubB;
	}
	
	public static PrivateKey getPrivateKeyAlice() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		Path path = Paths.get("AlicePri.txt");
		byte[] bytes = Files.readAllBytes(path);
		
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		priA = kf.generatePrivate(ks);
		return priA;
		
	}
	
	public static PrivateKey getPrivateKeyBob() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		Path path = Paths.get("BobPri.txt");
		byte[] bytes = Files.readAllBytes(path);
		
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		priB = kf.generatePrivate(ks);
		return priB;
		
	}
	
	public static void setSecretKey(SecretKey secret) {
		secretKey = secret;
	}
	
	public static SecretKey getSecretKey() {
		return secretKey;
	}

}
