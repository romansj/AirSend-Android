package com.cherrydev.airsend.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

public class NetworkUtils {

    @Deprecated(since="Use AirSendCore library NetworkUtils")
    private static String getLocalIpAddress(boolean acceptLoopback) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if ((inetAddress.isLoopbackAddress() == acceptLoopback) && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        return "null";
    }

    @Deprecated(since="Use AirSendCore library NetworkUtils")
    public static int getNetworkCount(boolean acceptLoopback) {
        int count = -1;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if ((inetAddress.isLoopbackAddress() == acceptLoopback) && inetAddress instanceof Inet4Address) {
                        count++;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        return count;
    }

    @Deprecated(since="Use AirSendCore library NetworkUtils")
    public static String getIPAddress(boolean acceptLoopback) {
        return getLocalIpAddress(acceptLoopback);
    }

    @Deprecated(since="Use AirSendCore library NetworkUtils")
    public static void printSupportedProtocols() {
        SSLParameters sslParameters;
        try {
            sslParameters = SSLContext.getDefault().getDefaultSSLParameters();
            // SSLv3, TLSv1, TLSv1.1, TLSv1.2 etc.
            String[] protocols = sslParameters.getProtocols();
            String s = Arrays.toString(protocols);
            Log.d("supported protocols", s);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        //not connected
        if (currentNetwork != null) return true;
        return false;
    }


    // https://stackoverflow.com/questions/2675362/how-to-find-an-available-port
    @Deprecated(since = "Use AirSendCore library NetworkUtils") // to find available port, pass "0", which is more efficient, but you cannot request a specific range.
    public static int nextFreePort(int from, int to) {
        int port = ThreadLocalRandom.current().nextInt(from, to);
        while (true) {
            if (isLocalPortFree(port)) {
                return port;
            } else {
                port = ThreadLocalRandom.current().nextInt(from, to);
            }
        }
    }

    @Deprecated(since = "Use AirSendCore library NetworkUtils")
    private static boolean isLocalPortFree(int port) {
        try {
            new ServerSocket(port).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
