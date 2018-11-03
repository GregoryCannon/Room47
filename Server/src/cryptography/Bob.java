package cryptography;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import cryptography.Packets.EstablishCommPacket;
import cryptography.Packets.MessagePacket;

public class Bob {
	public static final String BOB = "Bob";
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private DataInputStream bIn;
	private SecretKey sessionKey;

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());
		int port = 3000;
		Bob bob = new Bob();
		try{
			bob.start(port);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void start(int port) throws IOException, GeneralSecurityException, ClassNotFoundException {
		System.out.println("Server is listening");
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		bIn = new DataInputStream(clientSocket.getInputStream());
		System.out.println("Accepted connection.");

		acceptConnection();
		receiveMessages();
	}

	private void acceptConnection() throws ClassNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException{
		int len = Math.min(bIn.readInt(), 1024);  // Protect against buffer overflow by capping at 1024
		byte[] line = new byte[len];
		bIn.read(line);

		EstablishCommPacket p = (EstablishCommPacket) Serializer.deserialize(line);

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
	}

	private boolean checkSignature(EstablishCommPacket p){
		byte[] verifySig = Crypto.signEstablishPacket(p.name, p.time, p.enc);
		for (int i = 0; i < verifySig.length; i++){
			if (p.signature[i] != verifySig[i]) return false;
		}
		return true;
	}

	private void receiveMessages() throws IOException, GeneralSecurityException, ClassNotFoundException{
		byte[] ivBytes;
		byte[] ciphertextBytes;
		String plaintext;
		byte[] bobMac;
		String bobMacStr;

		while (true) {
			int len = Math.min(bIn.readInt(), 1024);  // Protect against buffer overflow by capping at 1024
			byte[] line = new byte[len];
			bIn.read(line);
			System.out.print("\nReceived data: " + new String(line, Charset.forName("ISO-8859-1")));
			//for (byte b : line) System.out.print(String.format("0x%02X ", b));
			System.out.println();

			MessagePacket p = (MessagePacket) Serializer.deserialize(line);

			ivBytes = Base64.decode(p.iv);
			ciphertextBytes = Base64.decode(p.cipherText);
			plaintext = new String(Crypto.cbcDecrypt(sessionKey, ivBytes, ciphertextBytes));
			bobMac = Crypto.calculateHmac(sessionKey, plaintext.getBytes());
			bobMacStr = Base64.toBase64String(bobMac).substring(0, 24);

			if (bobMacStr.equals(p.hMacStr)) {
				System.out.println("MAC check passed.");
			} else {
				System.out.println("MAC check FAILED!");
			}
			System.out.println("Decrypted: " + plaintext);
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
}
