package com.cherrydev.airsendcore.core;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class OwnerProperties {
    private int port; // where the server can reply
    private String name; // identity- name
    private String clientType; // android/windows


    public OwnerProperties(int port, String name, String deviceType) {
        this.port = port;
        this.name = name;
        this.clientType = deviceType;
    }

    public static OwnerProperties fromReceived(String userMessage) {
        if (userMessage == null) return null;

        String[] array = userMessage.split(",");
        List<String> list = Arrays.asList(array);
        if (list.size() != 3) return null;

        int connectedPort = Integer.parseInt(list.get(0)); // C# reports fake port from stream
        String connectedDeviceType = list.get(1); // W / A
        String connectedName = list.get(2); // user given name

        return new OwnerProperties(connectedPort, connectedName, connectedDeviceType);
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


    @NonNull
    public String getOwnerPropertiesString() {
        return getPort() + "," + getClientType() + "," + getName();
    }
}
