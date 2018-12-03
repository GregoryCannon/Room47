package SSLPackage;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

public class SslClient implements SslContextProvider {
    public static SSLSocket socket;
    public static OutputStream os;
    public static InputStream is;
    final int READ_LENGTH = 1024;
    Context context;
    Object[] objects;

    public SslClient(String host, int port, Context context) throws ExecutionException, InterruptedException {
        objects = new Object[2];
        objects[0] = host;
        objects[1] = port;
        this.context = context;
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//        StrictMode.setThreadPolicy(policy);
        try {
            socket = createSSLSocket((String) objects[0], (Integer) objects[1]);
            os = socket.getOutputStream();
            is = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendClientPacket(ClientPacket clientPacket) throws IOException {
        String s = new String(Serializer.serialize(clientPacket));
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

    public String readString() throws IOException {
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

    public void close() {
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
        AssetManager am = context.getAssets();
        InputStream is = am.open("client.bks");
        return SslUtil.createKeyManagers(is, "F8urious".toCharArray());
    }

    @Override
    public String getProtocol() {
        return "TLSv1.2";
    }

    @Override
    public TrustManager[] getTrustManagers() throws GeneralSecurityException, IOException {
        AssetManager am = context.getAssets();
        InputStream is = am.open("cacert.bks");
        return SslUtil.createTrustManagers(is, "F8urious".toCharArray());
    }

    private SSLSocket createSSLSocket(String host, int port) throws Exception {
        return SslUtil.createSSLSocket(host, port, this);
    }
}
