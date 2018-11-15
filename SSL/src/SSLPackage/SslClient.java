package SSLPackage;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class SslClient implements SslContextProvider {
    SSLSocket socket;
    OutputStream os;
    InputStream is;
    final int READ_LENGTH = 1024;

    public SslClient(String host, int port) {
        try {
            socket = createSSLSocket(host, port);
            os = socket.getOutputStream();
            is = socket.getInputStream();
            System.out.printf("Connected to server (%s). Writing ping...%n", SslUtil.getPeerIdentity(socket));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendClientPacket(ClientPacket clientPacket) throws IOException{
        sendBytes(Serializer.serialize(clientPacket));
    }

    public void sendBytes(byte[] bytes) throws IOException {
        byte[] toSend = new byte[SslUtil.READ_LENGTH];
        System.arraycopy(bytes, 0, toSend, 0, bytes.length);
        os.write(toSend);
        os.flush();
    }

    public ServerPacket readServerPacket() throws IOException, ClassNotFoundException {
        return (ServerPacket) Serializer.deserialize(readBytes(SslUtil.READ_LENGTH));
    }

    public String readString() throws IOException{
        return new String(readBytes(SslUtil.READ_LENGTH)).replaceAll("\0", "");
    }

    public byte[] readBytes(int len) throws IOException {
        byte[] buf = new byte[len];
        int read = is.read(buf);
        if (read != len) {
            throw new RuntimeException("Not enough bytes read: " + read + ", expected " + len + " bytes!");
        }
        return buf;
    }

    public void close(){
        try {
            os.close();
            is.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        SSL Logistics
     */

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

    private SSLSocket createSSLSocket(String host, int port) throws Exception {
        return SslUtil.createSSLSocket(host, port, this);
    }
}
