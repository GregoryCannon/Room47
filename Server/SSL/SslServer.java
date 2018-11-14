//import SslContextProvider;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;



public class SslServer implements SslContextProvider {
    ServerSocket socket;
    OutputStream os;
    InputStream is;


    public SslServer(int port, SslServerHandler handler){
        try {
            socket = createSSLSocket(port);
            System.out.println("Server started. Awaiting client...");

            SSLSocket client = (SSLSocket) socket.accept();
            os = client.getOutputStream();
            is = client.getInputStream();
            System.out.printf("Client (%s) connected. Awaiting ping...%n", SslUtil.getPeerIdentity(client));

            run(handler);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void run(SslServerHandler handler) throws Exception {
        while (true){
            ClientPacket cp = (ClientPacket) Serializer.deserialize(readBytes());
            ServerPacket response = handler.handlePacket(cp);
            sendBytes(Serializer.serialize(response));

            /*
            Old stuff from when it was only strings
            // Read from client
            String command = readString();
            System.out.println("Received: " + command);

            // Send response
            String response = handler.handleString(command);
            sendBytes(response.getBytes());
            System.out.println("Sent: " + response);
            */
        }
    }

    public void sendBytes(byte[] bytes) throws IOException {
        byte[] toSend = new byte[SslUtil.READ_LENGTH];
        System.arraycopy(bytes, 0, toSend, 0, bytes.length);
        os.write(toSend);
        os.flush();
    }

    public String readString() throws IOException{
        return new String(readBytes()).replaceAll("\0", "");
    }

    public byte[] readBytes() throws IOException {
        int len = SslUtil.READ_LENGTH;
        byte[] buf = new byte[len];
        int read = is.read(buf);
        if (read != len) {
            throw new RuntimeException("Not enough bytes read: " + read + ", expected " + len + " bytes!");
        }
        return buf;
    }

    @Override
    public KeyManager[] getKeyManagers() throws GeneralSecurityException, IOException {
        return SslUtil.createKeyManagers("server.jks", "F8urious".toCharArray());
    }

    @Override
    public String getProtocol() {
        return "TLSv1.2";
    }

    @Override
    public TrustManager[] getTrustManagers() throws GeneralSecurityException, IOException {
        return SslUtil.createTrustManagers("cacert.jks", "F8urious".toCharArray());
    }

    private ServerSocket createSSLSocket(int port) throws Exception {
        SSLServerSocket socket = SslUtil.createSSLServerSocket(port, this);
        socket.setNeedClientAuth(true);
        return socket;
    }
}
