package cryptography;

import java.io.BufferedReader;
import java.io.DataInputStream;
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

import cryptography.Packets.EstablishCommPacket;

public class Bob {
	public static final String BOB = "Bob";
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private DataInputStream bIn;
	private String clientMessage;
	private int configuration = 4;
	private SecretKey sessionKey;

	public void start(int port) throws IOException, GeneralSecurityException {
		System.out.println("Server is listening");
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		bIn = new DataInputStream(clientSocket.getInputStream());

		System.out.println("Accepted connection.");
		
		acceptConnection();
		receiveMessages();
		
	}

	private void acceptConnection(){
		try{
			int len = Math.min(bIn.readInt(), 1024);  // Protect against buffer overflow by capping at 1024
			byte[] line = new byte[len];
			bIn.read(line);
			System.out.println("line " + line);
			
			EstablishCommPacket p = (EstablishCommPacket) Serializer.deserialize(line);
			System.out.println("estpack " + p.name + " " + p.time + " " + p.enc + " " + p.signature);
			
			boolean timeValid = Math.abs(System.currentTimeMillis() - p.time) < 120000;
			boolean sigValid = checkSignature(p);
			
			if (sigValid && timeValid){
				System.out.println("Session key established!");
				sessionKey = (SecretKey) Crypto.decryptSessionKey(Keys.getPrivateKeyBob(), p.enc);
			} else {
				System.out.println("Invalid signature or time, failed to accept connection.");
				System.out.println("sig " + sigValid);
				System.out.println("time " + timeValid);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private boolean checkSignature(EstablishCommPacket p){
		byte[] verifySig = Crypto.signEstablishPacket(p.name, p.time, p.enc);
		for (int i = 0; i < verifySig.length; i++){
			if (p.signature[i] != verifySig[i]) return false;
		}
		return true;
	}
	
	private void receiveMessages() throws IOException, GeneralSecurityException{
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
			clientMessage = in.readLine();
			System.out.println("Client's message: " + clientMessage);
			
			// Case 4
			/*sKeyPath = Paths.get("SessionKey.txt");
			bytes = Files.readAllBytes(sKeyPath);
			SecretKey sKey = new SecretKeySpec(bytes, "AES");*/
			iv = clientMessage.substring(0, 24);
		    malloryPlaintextInd = clientMessage.length() - 24;
		    ciphertext = clientMessage.substring(24, malloryPlaintextInd);
		    ivBytes = Base64.decode(iv);
		    ciphertextBytes = Base64.decode(ciphertext);
		    plaintext = new String(Crypto.cbcDecrypt(sessionKey, ivBytes, ciphertextBytes));
			aliceMacStr = clientMessage.substring(malloryPlaintextInd);
			bobMac = Crypto.calculateHmac(sessionKey, plaintext.getBytes());
			bobMacStr = Base64.toBase64String(bobMac);
			bobMacStr = bobMacStr.substring(0, 24);
			clientMessage = plaintext;
			if (bobMacStr.equals(aliceMacStr)) {
				System.out.println("MAC check passed.");
			} else {
				System.out.println("MAC check FAILED!");
			}
			System.out.println("Decrypted: " + clientMessage);
			out.println("Message received.");
		}
	}
	
	/*
	public void stop() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
	}*/

	

	/*
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
				&& Math.abs(System.currentTimeMillis() - tA) < 120000) {
			sessionKey = decryptSessionKey(Keys.getPrivateKeyBob(), enc);
			Alice.sentBob(sessionKey);
		}

	}
	*/



	public static void main(String[] args) throws IOException, GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());
		//int port = Integer.parseInt(args[0]); // 6667
		int port = 3000;
		Bob bob = new Bob();
		bob.start(port);
	}
}
