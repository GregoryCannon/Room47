package cryptography;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import cryptography.Packets.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

public class Alice {
	public static final String ALICE = "Alice";
	private Socket clientSocket;
	private PrintWriter out;
	private DataOutputStream bOut;
	private BufferedReader in;
	private SecretKey sessionKey;

	public static void main(String args[]) throws IOException, GeneralSecurityException, ClassNotFoundException {
		Security.addProvider(new BouncyCastleProvider());
		//String host = args[0]; // 127.0.0.1
		//int port = Integer.parseInt(args[1]); // 6666
		String host = "127.0.0.1";
		int port = 3000;
		Alice alice = new Alice();
		
		alice.establishConnection(host, port);
		alice.sendMessages();
	}

	public void establishConnection(String ip, int port) throws IOException, GeneralSecurityException, ClassNotFoundException {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		bOut = new DataOutputStream(clientSocket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		// Send establishment packet
		sessionKey = Crypto.generateSessionKey();
		//String stringKey = Base64.toBase64String(sKey.getEncoded());
		//System.out.println("\n\n"+stringKey);
		//byte[] encodedKey = Base64.decode("Z2+uQRwPCI0z3gvh6wY79w==");
	    //sessionKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		
		long time = System.currentTimeMillis();
		byte[] encSessionKey = Crypto.encryptSessionKey(cryptography.Keys.getPublicKeyBob(), sessionKey);
		byte[] signature = Crypto.signEstablishPacket(ALICE, time, encSessionKey);
		EstablishCommPacket p = new EstablishCommPacket(ALICE, time, encSessionKey, signature);
		byte[] pEnc = Serializer.serialize(p);
		
		//System.out.println("Initial packet: " + p.name + " " + p.time + " " + p.enc + " " + p.signature);
		
		sendMessage("" + pEnc.length); 
		sendBytes(pEnc);
	}

	public void sendMessage(String msg) throws IOException {
		out.println(msg);
	}
	
	public void sendBytes(byte[] b) throws IOException {
		bOut.write(b);
	}

	public void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}

	public void sendMessages() throws IOException, GeneralSecurityException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Type in a message to send to Bob:");
		String message = scan.nextLine();
		while(message != "q") {
			MessagePacket p = new MessagePacket(message, sessionKey);
			byte[] pEnc = Serializer.serialize(p);
			sendMessage("" + pEnc.length);
			sendBytes(pEnc);
			
			System.out.println("Type in a message to send to Bob:");
			message = scan.nextLine();
		}
		scan.close();
	}


}
