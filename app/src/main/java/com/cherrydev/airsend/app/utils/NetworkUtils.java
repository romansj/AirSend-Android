package com.cherrydev.airsend.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

public class NetworkUtils {

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        return currentNetwork != null;
    }

}
