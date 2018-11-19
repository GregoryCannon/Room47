package SSLPackage;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;

import SSLPackage.ClientPacket;
import SSLPackage.Serializer;
import SSLPackage.ServerPacket;
import SSLPackage.SslContextProvider;
import SSLPackage.SslServerHandler;
import SSLPackage.SslUtil;


public class SslServer implements SslContextProvider {
    ServerSocket socket;
    OutputStream os;
    InputStream is;


    public SslServer(int port, SslServerHandler handler){
        try {
            socket = createSSLSocket(port);
            System.out.println("Server: Server started. Awaiting client...");

            SSLSocket client = (SSLSocket) socket.accept();
            os = client.getOutputStream();
            is = client.getInputStream();
            run(handler);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void run(SslServerHandler handler) throws Exception {
        while (true){
            System.out.println("Server: packet received");
            ClientPacket cp = (ClientPacket) Serializer.deserialize(readBytes());
            ServerPacket response = handler.handlePacket(cp);
            sendBytes(Serializer.serialize(response));
            System.out.println("Server: response sent");
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
        SSL Setup
     */

    @Override
    public KeyManager[] getKeyManagers() throws GeneralSecurityException, IOException {
        InputStream is = null;
        return SslUtil.createKeyManagers(is, "F8urious".toCharArray());
    }

    @Override
    public String getProtocol() {
        return "TLSv1.2";
    }

    @Override
    public TrustManager[] getTrustManagers() throws GeneralSecurityException, IOException {
        InputStream is=null;
        return SslUtil.createTrustManagers(is, "F8urious".toCharArray());
    }

    private ServerSocket createSSLSocket(int port) throws Exception {
        SSLServerSocket socket = SslUtil.createSSLServerSocket(port, this);
        socket.setNeedClientAuth(true);
        return socket;
    }
}
