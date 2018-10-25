package cryptography;
//package cryptography;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Generator {
	private static Key pubA;
	private static Key priA;
	private static Key pubB;
	private static Key priB;
	
	public static void generateKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator kpgAlice = KeyPairGenerator.getInstance("RSA");
		kpgAlice.initialize(2048);
		KeyPair kpAlice = kpgAlice.generateKeyPair();
		
		pubA = kpAlice.getPublic();
		priA = kpAlice.getPrivate();
		
		KeyPairGenerator kpgBob = KeyPairGenerator.getInstance("RSA");
		kpgBob.initialize(2048);
		KeyPair kpBob = kpgBob.generateKeyPair();
		
		pubB = kpBob.getPublic();
		priB = kpBob.getPrivate();
	}
	
	public static void sendKeys() throws IOException {
		String outFileAlicePub = "AlicePub.txt";
		FileOutputStream outAPub = new FileOutputStream(outFileAlicePub);
		outAPub.write(pubA.getEncoded());
		outAPub.close();
		
		String outFileAlicePri = "AlicePri.txt";
		FileOutputStream outAPri = new FileOutputStream(outFileAlicePri);
		outAPri.write(priA.getEncoded());
		outAPri.close();
		
		String outFileBobPub = "BobPub.txt";
		FileOutputStream outBPub = new FileOutputStream(outFileBobPub);
		outBPub.write(pubB.getEncoded());
		outBPub.close();
		
		String outFileBobPri = "BobPri.txt";
		FileOutputStream outBPri = new FileOutputStream(outFileBobPri);
		outBPri.write(priB.getEncoded());
		outBPri.close();
		
		System.out.println("Filesystems set up");
		
	}
	
	public static SecretKey generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
				 KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "BC");
				 keyGenerator.init(128);
				 return keyGenerator.generateKey();
	}
	
	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}
		
	public static void main (String args[]) throws NoSuchAlgorithmException, IOException {
		Security.addProvider(new BouncyCastleProvider());
		generateKeys();
		sendKeys();
		
	}

}
