package com.cherrydev.airsend.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.service.ServerService;

import java.util.Arrays;
import java.util.List;

public class ClientManagerOwnerProperties {
    private int port; // where the server can reply
    private String name; // identity- name
    private String clientType; // android/windows


    public ClientManagerOwnerProperties(int port, String name, String deviceType) {
        this.port = port;
        this.name = name;
        this.clientType = deviceType;
    }

    public static ClientManagerOwnerProperties fromReceived(String userMessage) {
        if (userMessage == null) return null;

        String[] array = userMessage.split(",");
        List<String> list = Arrays.asList(array);
        if (list.size() != 3) return null;

        int connectedPort = Integer.parseInt(list.get(0)); // C# reports fake port from stream
        String connectedDeviceType = list.get(1); // W / A
        String connectedName = list.get(2); // user given name

        return new ClientManagerOwnerProperties(connectedPort, connectedName, connectedDeviceType);
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getClientType() {
        return clientType;
    }

    public static ClientManagerOwnerProperties getOwnerProperties(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String settingDeviceName = sharedPref.getString(context.getString(R.string.setting_device_name), Build.MODEL);
        ClientManagerOwnerProperties ownerProperties = new ClientManagerOwnerProperties(ServerService.getPORT(), settingDeviceName, "A");
        return ownerProperties;
    }

    @NonNull
    public String getOwnerPropertiesString() {
        return getPort() + "," + getClientType() + "," + getName();
    }
}
