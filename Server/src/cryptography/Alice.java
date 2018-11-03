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
	
	public void sendMessage(String message) throws IOException{
		StringMessage sm = new StringMessage(message);
		BytePacket bp = new BytePacket(Serializer.serialize(sm), sessionKey);
		byte[] bpEnc = Serializer.serialize(bp);
		sendString("" + bpEnc.length);
		sendBytes(bpEnc);
	}

	public void sendMessagesFromStdIn() throws IOException, GeneralSecurityException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Type in a message to send to Bob:");
		String message = scan.nextLine();
		while(message != "q") {
			sendMessage(message);
			
			System.out.println("Type in a message to send to Bob:");
			message = scan.nextLine();
		}
		scan.close();
	}


}
