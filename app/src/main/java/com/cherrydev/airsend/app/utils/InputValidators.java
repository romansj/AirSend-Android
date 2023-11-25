package com.cherrydev.airsend.app.utils;


import androidx.annotation.NonNull;

import com.google.common.net.InetAddresses;

import java.util.List;

public class InputValidators {

    private static List<String> validEmulatorIPs = List.of("10.0.2.1", "10.0.2.2", "10.0.2.3", "10.0.2.4", "10.0.2.5", "10.0.2.6", "10.0.2.7", "10.0.2.8", "10.0.2.9", "10.0.2.10", "10.0.2.11",
            "10.0.2.12", "10.0.2.13", "10.0.2.14", "10.0.2.15", "10.0.2.16", "10.0.2.17", "10.0.2.18", "10.0.2.19", "10.0.2.20", "10.0.2.21",
            "10.0.2.22", "10.0.2.23", "10.0.2.24");

    public static boolean validatePort(@NonNull String portText) {
        portText = portText.trim();
        if (portText.isEmpty()) return false;

        try {
            var i = Integer.parseInt(portText);
            if (i <= 0 || i > 65535) return false;

        } catch (NumberFormatException e) {
            return false;
        }


        return true;
    }

    public static boolean validateIP(@NonNull String IPText) {
        IPText = IPText.trim();
        if (IPText.isEmpty()) return false;

        var isIP = InetAddresses.isInetAddress(IPText);

        //if (BuildConfig.DEBUG && !isIP) {


        var contains = validEmulatorIPs.contains(IPText);
        //if (contains) return true;


        return isIP;
    }

    public static boolean validateConnectionParams(String ipText, String portText, String pskText) {
        return !validateIP(ipText) || !validatePort(portText) || validatePsk(pskText);
    }

    private static boolean validatePsk(String pskText) {
        return !pskText.trim().isEmpty();
    }
}
