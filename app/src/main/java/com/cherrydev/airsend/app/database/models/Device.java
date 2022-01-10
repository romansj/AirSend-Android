package com.cherrydev.airsend.app.database.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.cherrydev.airsend.core.Status;

@Entity
public class Device {


    @PrimaryKey
    @NonNull
    private String IP;
    private String name;
    private int port;
    private Status clientRunning = Status.NOT_RUNNING;
    private Status serverRunning = Status.UNKNOWN;
    private String deviceType;


    public Device(String name, @NonNull String IP, int port) {
        this.name = name;
        this.IP = IP;
        this.port = port;
        this.clientRunning = Status.UNKNOWN;
        this.serverRunning = Status.UNKNOWN;
        this.deviceType = "";
    }

    @Ignore
    public Device(String name, @NonNull String IP, int port, String deviceType, Status serverRunning) {
        this.name = name;
        this.IP = IP;
        this.port = port;
        this.clientRunning = Status.UNKNOWN;
        this.serverRunning = serverRunning;
        this.deviceType = deviceType;
    }


    @NonNull
    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public Status getClientRunning() {
        return clientRunning;
    }

    public void setClientRunning(Status clientRunning) {
        this.clientRunning = clientRunning;
    }


    public Status getServerRunning() {
        return serverRunning;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setServerRunning(Status serverRunning) {
        this.serverRunning = serverRunning;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public static void updateExistingDevice(Device deviceExisting, Device deviceNew) {
        deviceExisting.setPort(deviceNew.getPort());
        if (!deviceNew.getName().isEmpty()) deviceExisting.setName(deviceNew.getName());
        if (!deviceNew.getDeviceType().isEmpty()) deviceExisting.setDeviceType(deviceNew.getDeviceType());

        if (deviceNew.getClientRunning() != Status.UNKNOWN) deviceExisting.setClientRunning(deviceNew.getClientRunning());
        if (deviceNew.getServerRunning() != Status.UNKNOWN) deviceExisting.setServerRunning(deviceNew.getServerRunning());
    }

    public static boolean createNew(Device deviceNew) {
        return deviceNew.getServerRunning() != Status.NOT_RUNNING;
    }
}
