package cryptography;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient {
	public static void main(String[] args) throws IOException{
		String host = "127.0.0.1";
		Integer port = 8080;
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);
		
		InputStream in = sslsocket.getInputStream();
		OutputStream out = sslsocket.getOutputStream();
		
		out.write(1);
		while (in.available() > 0) {
			byte[] bytes = new byte[1024];
			int len = in.read(bytes, 0, 1024);
			byte[] inputBytes = Arrays.copyOfRange(bytes, 0, len);
			System.out.print(new String(inputBytes));
		}
		
		System.out.println("Secured connection performed successfully");
	}
}
