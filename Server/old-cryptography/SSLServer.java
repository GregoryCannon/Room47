package cryptography;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

public class SSLServer {
	public static void main(String[] argv) throws Exception {
		
		
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(null, "password".toCharArray());
		/*
	    InputStream ksIs = new FileInputStream("...");
	    try {
	        ks.load(ksIs, "password".toCharArray());
	    } finally {
	        if (ksIs != null) {
	            ksIs.close();
	        }
	    }*/

	    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
	            .getDefaultAlgorithm());
	    kmf.init(ks, "keypassword".toCharArray());
	    TrustManager[] trustAllCerts = new TrustManager[] {
	            new TrustManager() {
	                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	                }

	                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	                }

	                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }
	            }
	    };

	    // Install the all-trusting trust manager
	    SSLContext sc = SSLContext.getInstance("TLSv1.2");
	    sc.init(null, trustAllCerts, new SecureRandom());
	    SSLServerSocket ssl = (SSLServerSocket) sc.getServerSocketFactory().createServerSocket(
	            5000);
		
		
	    SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	    SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(8080);
	    String[] suites = serverSocket.getSupportedCipherSuites();
	    for (int i = 0; i < suites.length; i++) {
	      System.out.println(suites[i]);
	    }
	    System.out.println("----");
	    serverSocket.setEnabledCipherSuites(suites);
	    String[] protocols = serverSocket.getSupportedProtocols();
	    for (int i = 0; i < protocols.length; i++) {
	      System.out.println(protocols[i]);
	    }
	    System.out.println("----");
	    SSLSocket socket = (SSLSocket) serverSocket.accept();
	    socket.startHandshake();
	    System.out.println(socket.getRemoteSocketAddress());
	  }
}
