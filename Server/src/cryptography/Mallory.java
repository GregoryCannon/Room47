package cryptography;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import org.bouncycastle.util.encoders.Base64;

public class Mallory {

	// Server code
	private static ServerSocket serverSocket;
	private static Socket clientSocketAlice;
	private static PrintWriter outAlice;
	private static BufferedReader inAlice;
	private static String aliceMessage;
	private static String host;
	private static int port;

	public static void start(int serverPort) throws IOException {
		System.out.println("Server is listening");
		serverSocket = new ServerSocket(serverPort);
		clientSocketAlice = serverSocket.accept();
		System.out.println("Accepted connection from Alice");
		// connect to Bob and relay Alice's message
		startConnection(host, port);
		HashMap<String, String> oldMessages = new HashMap<>();
		while (true) {
			outAlice = new PrintWriter(clientSocketAlice.getOutputStream(), true);
			inAlice = new BufferedReader(new InputStreamReader(clientSocketAlice.getInputStream()));
			while((aliceMessage = inAlice.readLine()) != null) {
			// store Alice's message
			if (oldMessages.get(aliceMessage) == null) {
				oldMessages.put(aliceMessage, aliceMessage);
			}
			System.out.println("Should this message be forwarded, deleted, modified, or replayed?");
			Scanner scan = new Scanner(System.in);
			String message = scan.nextLine();
			switch (message) {
			case "replay":
				Random rand = new Random();
				int selectMessage = rand.nextInt(oldMessages.size());
				for(String key: oldMessages.keySet()) {
					if(selectMessage-- == 0) {
						aliceMessage = oldMessages.get(key);
						sendMessage(oldMessages.get(key));
						break;
					}
				}
				break;
			case "forward":
				sendMessage(aliceMessage);
				break;
			case "modify":
				sendMessage(aliceMessage);
				aliceMessage = "This message was tampered with. Bob may or may not be able to decrypt the message";
				break;
			case "delete":
				break;
			}
			System.out.println(aliceMessage);
		}
		}
	}

	public void stop() throws IOException {
		inAlice.close();
		outAlice.close();
		clientSocketAlice.close();
		serverSocket.close();
	}

	// Client code
	private static Socket clientSocketBob;
	private static PrintWriter outBob;
	private static BufferedReader inBob;

	public static void startConnection(String ip, int port) throws IOException {
		clientSocketBob = new Socket(ip, port);
		outBob = new PrintWriter(clientSocketBob.getOutputStream(), true);
		inBob = new BufferedReader(new InputStreamReader(clientSocketBob.getInputStream()));
	}

	public static String sendMessage(String msg) throws IOException {
		outBob.println(msg);
		String resp = inBob.readLine();
		return resp;
	}

	public void stopConnection() throws IOException {
		inBob.close();
		outBob.close();
		clientSocketBob.close();
	}

	public static void main(String[] args) throws IOException {
		host = args[0]; // 127.0.0.1
		port = Integer.parseInt(args[1]); // 6667
		start(6666);
	}
}