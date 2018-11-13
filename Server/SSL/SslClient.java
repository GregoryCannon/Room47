import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class SslClient implements SslContextProvider {

    public static void main(String[] args) throws Exception {
        /*
        if (args.length != 2) {
            System.out.println("Usage: SslClient <host> <port>\n");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        */
        String host = "localhost";
        int port = 9000;
        new SslClient().run(host, port);
    }

    @Override
    public KeyManager[] getKeyManagers() throws GeneralSecurityException, IOException {
        return SslUtil.createKeyManagers("client.jks", "F8urious".toCharArray());
    }

    @Override
    public String getProtocol() {
        return "TLSv1.2";
    }

    @Override
    public TrustManager[] getTrustManagers() throws GeneralSecurityException, IOException {
        return SslUtil.createTrustManagers("cacert.jks", "F8urious".toCharArray());
    }

    public void run(String host, int port) throws Exception {
        try (SSLSocket socket = createSSLSocket(host, port); OutputStream os = socket.getOutputStream(); InputStream is = socket.getInputStream()) {

            System.out.printf("Connected to server (%s). Writing ping...%n", SslUtil.getPeerIdentity(socket));

            os.write("ping".getBytes());
            os.flush();

            System.out.println("Ping written, awaiting pong...");

            byte[] buf = new byte[4];
            int read = is.read(buf);
            if (read != 4) {
                throw new RuntimeException("Not enough bytes read: " + read + ", expected 4 bytes!");
            }

            String response = new String(buf);
            if (!"pong".equals(response)) {
                throw new RuntimeException("Expected 'pong', but got '" + response + "'...");
            }

            System.out.println("Pong obtained! Ending client...");
        }
    }

    private SSLSocket createSSLSocket(String host, int port) throws Exception {
        return SslUtil.createSSLSocket(host, port, this);
    }
}
