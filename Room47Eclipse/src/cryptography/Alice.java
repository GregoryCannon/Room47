package cryptography;
//package cryptography;

import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

public class Alice {
	public static final String ALICE = "Alice";
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private static boolean verified = false;
	private static int configuration = 4;
	private static byte [] hMac;

	public void startConnection(String ip, int port) throws IOException {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	public void sendMessage(String msg) throws IOException {
		out.println(msg);
	}

	public void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}

	public static byte[] encryptSessionKey(PublicKey rsaPublic, SecretKey secret) throws NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		Cipher c = Cipher.getInstance("RSA/NONE/OAEPPadding", "BC");
		c.init(Cipher.WRAP_MODE, rsaPublic);
		return c.wrap(secret);

	}

	public static byte[] generateSignature(PrivateKey dsaPrivate, byte[] input) throws GeneralSecurityException {
		Signature signature = Signature.getInstance("SHA384withRSA", "BC");
		signature.initSign(dsaPrivate);
		signature.update(input);
		return signature.sign();
	}

	public static void verification() throws IOException, GeneralSecurityException {
		SecretKey sKey = cryptography.Generator.generateSessionKey();
		String secretKey = "SessionKey.txt";
		FileOutputStream outSecret = new FileOutputStream(secretKey);
		byte[] secret = sKey.getEncoded();
		outSecret.write(secret);
		outSecret.close();
		cryptography.Keys.setSecretKey(sKey);
		byte[] enc = encryptSessionKey(cryptography.Keys.getPublicKeyBob(), sKey);
		String encString = new String(enc, "UTF-8");
		String params = cryptography.Bob.BOB + cryptography.Generator.getCurrentTime() + encString;
		byte[] p = params.getBytes();
		byte[] signature = generateSignature(cryptography.Keys.getPrivateKeyAlice(), p);
		cryptography.Bob.sentAlice(cryptography.Bob.BOB, cryptography.Generator.getCurrentTime(), enc, p, signature);
	}

	public static void sentBob(Key sessionKey) {
		if (sessionKey.equals(cryptography.Keys.getSecretKey())) {
			verified = true;
		}
	}

	public static boolean verifyMac(byte [] mac) {
		if (hMac==mac) {
			return true;
		}
		return false;
	}

	public void sendMessages() throws IOException, GeneralSecurityException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Type in a message to send to Bob:");
		String message = scan.nextLine();
		byte[][] ivAndCipher = null;
		String iv;
		String ciphertext;
		String hMacStr;
		while(message != "q") {
		switch (configuration) {
		case 1:
			break;
		case 2:
			ivAndCipher = cbcEncrypt(cryptography.Keys.getSecretKey(), message.getBytes()); // used to be message.getBytes()
			//byte[] see = Bob.cbcDecrypt(Keys.getSecretKey(), ivAndCipher[0], ivAndCipher[1]);
			iv = Base64.toBase64String(ivAndCipher[0]);
			ciphertext = Base64.toBase64String(ivAndCipher[1]);
			message = iv + ciphertext;
			System.out.println("Message in Alice " + message);
			break;
		case 3:
			hMac = calculateHmac(cryptography.Keys.getSecretKey(), message.getBytes());
			hMacStr = Base64.toBase64String(hMac);
			hMacStr = hMacStr.substring(0, 24);
			message+= hMacStr;
			break;
		case 4:
			hMac = calculateHmac(cryptography.Keys.getSecretKey(), message.getBytes());
			hMacStr = Base64.toBase64String(hMac);
			hMacStr = hMacStr.substring(0, 24);
			ivAndCipher = cbcEncrypt(cryptography.Keys.getSecretKey(), message.getBytes()); // used to be message.getBytes()
			//byte[] see = Bob.cbcDecrypt(Keys.getSecretKey(), ivAndCipher[0], ivAndCipher[1]);
			iv = Base64.toBase64String(ivAndCipher[0]);
			ciphertext = Base64.toBase64String(ivAndCipher[1]);
			message = iv + ciphertext + hMacStr;
			break;
		}
		sendMessage(message);
		System.out.println("Type in a message to send to Bob:");
		message = scan.nextLine();
		}
		scan.close();
	}

	//encrypt 
	public static byte[][] cbcEncrypt(SecretKey key, byte[] data) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		String randIV = RandomStringUtils.random(16, 0, 16, true, true, "0123456789abcdef".toCharArray());
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(randIV.getBytes()));
		return new byte[][] { cipher.getIV(), cipher.doFinal(data) };
	}
	//mac
	public static byte[] calculateHmac(SecretKey key, byte[] data) throws GeneralSecurityException {
		Mac hmac = Mac.getInstance("HMacSHA512", "BC"); hmac.init(key);
		return hmac.doFinal(data);
		}

	public static void main(String args[]) throws IOException, GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());
		String host = args[0]; // 127.0.0.1
		int port = Integer.parseInt(args[1]); // 6666
		Alice alice = new Alice();
		alice.startConnection(host, port);
		verification();
		alice.sendMessages();
	}
}
