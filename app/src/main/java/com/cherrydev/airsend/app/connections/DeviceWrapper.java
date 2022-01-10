package com.cherrydev.airsend.app.connections;

import com.cherrydev.airsend.app.database.models.Device;

public class DeviceWrapper {
    private Device device;
    private long id;

    public DeviceWrapper(long id, Device device) {
        this.device = device;
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public long getId() {
        return id;
    }

}
