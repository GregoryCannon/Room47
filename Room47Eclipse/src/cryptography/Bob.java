package cryptography;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

public class Bob {
	public static final String BOB = "Bob";
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static PrintWriter out;
	private static BufferedReader in;
	private static String malloryMessage;
	private static int configuration = 4;
	private static Key sessionKey;

	public static void start(int port) throws IOException, GeneralSecurityException {
		System.out.println("Server is listening");
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		System.out.println("Accepted connection from Mallory");
		String iv;
		String ciphertext;
		byte[] ivBytes;
		byte[] ciphertextBytes;
		Path sKeyPath;
		byte[] bytes;
		int malloryPlaintextInd;
		String plaintext;
		String aliceMacStr;
		byte[] bobMac;
		String bobMacStr;
		
		while (true) {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			malloryMessage = in.readLine(); // fix if doesn't work
			System.out.println("Mallory's message " + malloryMessage);
			
			// Case 4
			sKeyPath = Paths.get("SessionKey.txt");
			bytes = Files.readAllBytes(sKeyPath);
			SecretKey sKey = new SecretKeySpec(bytes, "AES");
			iv = malloryMessage.substring(0, 24);
		    malloryPlaintextInd = malloryMessage.length() - 24;
		    ciphertext = malloryMessage.substring(24, malloryPlaintextInd);
		    ivBytes = Base64.decode(iv);
		    ciphertextBytes = Base64.decode(ciphertext);
		    plaintext = new String(cbcDecrypt(sKey, ivBytes, ciphertextBytes));
			aliceMacStr = malloryMessage.substring(malloryPlaintextInd);
			bobMac = Alice.calculateHmac(sKey, plaintext.getBytes());
			bobMacStr = Base64.toBase64String(bobMac);
			bobMacStr = bobMacStr.substring(0, 24);
			malloryMessage = plaintext;
			if (!bobMacStr.equals(aliceMacStr)) {
				malloryMessage+= " THIS MESSAGE HAS A DIFFERENT MAC";
			}
			out.println(malloryMessage);
			System.out.println(malloryMessage);
		}
	}

	public void stop() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
	}

	public static Key decryptSessionKey(PrivateKey rsaPrivate, byte[] wrappedKey)
			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
		Cipher c = Cipher.getInstance("RSA/NONE/OAEPPadding", "BC");
		c.init(Cipher.UNWRAP_MODE, rsaPrivate);
		return c.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
	}

	public static boolean verifySignature(PublicKey dsaPublic, byte[] input, byte[] encSignature)
			throws GeneralSecurityException {
		Signature signature = Signature.getInstance("SHA384withRSA", "BC");
		signature.initVerify(dsaPublic);
		signature.update(input);
		return signature.verify(encSignature);
	}

	public static void sentAlice(String Bob, long tA, byte[] enc, byte[] params, byte[] signature)
			throws NoSuchAlgorithmException, InvalidKeySpecException, GeneralSecurityException, IOException {
		if (verifySignature(Keys.getPublicKeyAlice(), params, signature)
				&& Math.abs(Generator.getCurrentTime() - tA) < 120000) {
			sessionKey = decryptSessionKey(Keys.getPrivateKeyBob(), enc);
			Alice.sentBob(sessionKey);
		}

	}

	// decrypt
	public static byte[] cbcDecrypt(SecretKey key, byte[] iv, byte[] cipherText) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(cipherText);
	}

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());
		int port = Integer.parseInt(args[0]); // 6667
		start(port);

	}
}
