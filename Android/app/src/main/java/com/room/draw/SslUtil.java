package com.room.draw;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SslUtil {
    private static final String JKS = "BKS";
    public static final int READ_LENGTH = 1024;

    public static KeyManager[] createKeyManagers(InputStream keystore, char[] password) throws GeneralSecurityException, IOException {
        return createKeyManagers(keystore, password, password);
    }

    public static KeyManager[] createKeyManagers(InputStream ksIs, char[] storePassword, char[] keyPassword) throws GeneralSecurityException, IOException {
        String algorithm = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);

        KeyStore ks = KeyStore.getInstance(JKS);
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
        socket.setEnabledProtocols(new String[] { provider.getProtocol() });
        socket.setNeedClientAuth(false);
        return socket;
    }

    public static SSLSocket createSSLSocket(String host, int port, SslContextProvider provider) throws Exception {
        SSLContext context = createSSLContext(provider);
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setEnabledProtocols(new String[] { provider.getProtocol() });
        return socket;
    }

    public static TrustManager[] createTrustManagers(InputStream ksIs, char[] password) throws GeneralSecurityException, IOException {
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);

        KeyStore ks = KeyStore.getInstance(JKS);
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
