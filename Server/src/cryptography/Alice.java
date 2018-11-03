package cryptography;

import cryptography.Packets.BytePacket;
import cryptography.Packets.EstablishCommPacket;
import cryptography.Packets.StringMessage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Scanner;

import static cryptography.Mallory.sendMessage;

public class Alice {
	public static final String ALICE = "Alice";
	private Socket clientSocket;
	private PrintWriter stringOut;
	private DataOutputStream byteOut;
	private BufferedReader stringIn;
	private SecretKey sessionKey;

	public static void main(String args[]) throws IOException, GeneralSecurityException, ClassNotFoundException {
		Security.addProvider(new BouncyCastleProvider());
		String host = "127.0.0.1";
		int port = 3000;
		Alice alice = new Alice();
		
		alice.establishConnection(host, port);
		alice.sendMessagesFromStdIn();
	}

	public void establishConnection(String ip, int port) throws IOException, GeneralSecurityException, ClassNotFoundException {
		clientSocket = new Socket(ip, port);
		stringOut = new PrintWriter(clientSocket.getOutputStream(), true);
		byteOut = new DataOutputStream(clientSocket.getOutputStream());
		stringIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		sessionKey = Crypto.generateSessionKey();
		
		long time = System.currentTimeMillis();
		byte[] encSessionKey = Crypto.encryptSessionKey(cryptography.Keys.getPublicKeyBob(), sessionKey);
		byte[] signature = Crypto.signEstablishPacket(ALICE, time, encSessionKey);
		EstablishCommPacket p = new EstablishCommPacket(ALICE, time, encSessionKey, signature);
		byte[] pEnc = Serializer.serialize(p);
		
		sendString("" + pEnc.length); 
		sendBytes(pEnc);
	}

	private void sendString(String msg) throws IOException {
		stringOut.println(msg);
	}
	
	private void sendBytes(byte[] b) throws IOException {
		byteOut.write(b);
	}

	public void stopConnection() throws IOException {
		stringIn.close();
		stringOut.close();
		byteOut.close();
		clientSocket.close();
	}
	
	public void sendStringMessage(String message) throws IOException{
		StringMessage sm = new StringMessage(message);
		byte[] smEnc = Serializer.serialize(sm);
		sendBytePacket(smEnc);
	}

	public void sendBytePacket(byte[] bytes) throws IOException {
		BytePacket bp = new BytePacket(bytes, sessionKey);
		byte[] bpEnc = Serializer.serialize(bp);
		sendString("" + bpEnc.length);
		sendBytes(bpEnc);
	}

	public void sendMessagesFromStdIn() throws IOException, GeneralSecurityException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Type in a message to send to Bob:");
		String message = scan.nextLine();
		while(message != "q") {
			sendStringMessage(message);
			
			System.out.println("Type in a message to send to Bob:");
			message = scan.nextLine();
		}
		scan.close();
	}


}
