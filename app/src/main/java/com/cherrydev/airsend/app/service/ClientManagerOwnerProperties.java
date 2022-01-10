package com.cherrydev.airsend.app.service;

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
}
