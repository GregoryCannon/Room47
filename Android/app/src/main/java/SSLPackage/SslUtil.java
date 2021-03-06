package SSLPackage;

import javax.net.ssl.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class SslUtil {
    private static final String BKS = "BKS";
    public static final int READ_LENGTH = 1024;

    // Our own custom list of supported cipher suites, which disallows all 3DES variants and "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
    public static final String[] ENABLED_CIPHER_SUITES = new String[]{
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_RSA_WITH_AES_128_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_RSA_WITH_AES_128_GCM_SHA256"

    };

    public static KeyManager[] createKeyManagers(InputStream keystore, char[] password) throws GeneralSecurityException, IOException {
        return createKeyManagers(keystore, password, password);
    }

    public static KeyManager[] createKeyManagers(InputStream ksIs, char[] storePassword, char[] keyPassword) throws GeneralSecurityException, IOException {
        String algorithm = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);

        KeyStore ks = KeyStore.getInstance(BKS);
        try {
            ks.load(ksIs, storePassword);
        }
        finally {
            if (ksIs != null) {
                ksIs.close();
            }
        }
        kmf.init(ks, keyPassword);

        return kmf.getKeyManagers();
    }

    public static SSLContext createSSLContext(SslContextProvider provider) throws Exception {
        SSLContext context = SSLContext.getInstance(provider.getProtocol());
        context.init(provider.getKeyManagers(), provider.getTrustManagers(), new SecureRandom());
        return context;
    }

    public static SSLServerSocket createSSLServerSocket(int port, SslContextProvider provider) throws Exception {
        SSLContext context = createSSLContext(provider);
        SSLServerSocketFactory factory = context.getServerSocketFactory();
        SSLServerSocket socket = (SSLServerSocket) factory.createServerSocket(port);
        socket.setEnabledCipherSuites(ENABLED_CIPHER_SUITES);
        socket.setEnabledProtocols(new String[] { provider.getProtocol() });
        socket.setNeedClientAuth(false);
        return socket;
    }

    public static SSLSocket createSSLSocket(String host, int port, SslContextProvider provider) throws Exception {
        SSLContext context = createSSLContext(provider);
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setEnabledCipherSuites(ENABLED_CIPHER_SUITES);
        socket.setEnabledProtocols(new String[] { provider.getProtocol() });
        return socket;
    }

    public static TrustManager[] createTrustManagers(InputStream ksIs, char[] password) throws GeneralSecurityException, IOException {
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);

        KeyStore ks = KeyStore.getInstance(BKS);
        try {
            ks.load(ksIs, password);
        }
        finally {
            if (ksIs != null) {
                ksIs.close();
            }
        }

        tmf.init(ks);

        return tmf.getTrustManagers();
    }
}
