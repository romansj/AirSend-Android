package com.cherrydev.airsendcore.utils;


import com.cherrydev.airsendcore.core.Constants;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


public class SSLUtils {
    // Create the and initialize the SSLContext
    public static SSLContext createSSLContext(InputStream stream, char[] password) {
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(stream, password);

            stream.close();

            // Create key manager
            //getInstance("SunX509") --> getInstance(KeyManagerFactory.getDefaultAlgorithm()) --- even though it calls the same "SunX509"
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            // Create trust manager
            //getInstance("SunX509") --> getInstance(TrustManagerFactory.getDefaultAlgorithm()) --- even though it calls the same "SunX509"
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance(Constants.protocol);
            sslContext.init(keyManagers, trustManagers, null);
            //sslContext.createSSLEngine();

            return sslContext;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    public static String getSocketInfo(SSLSocket sslSocket) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("isConnected ").append(sslSocket.isConnected()).append(", ");
        stringBuilder.append("isClosed ").append(sslSocket.isClosed()).append(", ");
        stringBuilder.append("isInputShutdown ").append(sslSocket.isInputShutdown()).append(", ");
        stringBuilder.append("isOutputShutdown ").append(sslSocket.isOutputShutdown()).append(", ");

        stringBuilder.append("getSession().isValid() ").append(sslSocket.getSession().isValid());


        return stringBuilder.toString();
    }


    public static String getSocketIPAddress(SSLSocket sslSocket) {
        return sslSocket.getInetAddress().toString().substring(1);
    }
}
