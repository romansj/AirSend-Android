package com.cherrydev.airsend.app.connections;

import com.cherrydev.airsend.app.utils.DialogRecyclerViewAction;

public class DeviceActionWrapper implements DialogRecyclerViewAction.DialogActionItemInterface {
    private DeviceAction deviceAction;
    private String text;

    public DeviceActionWrapper(DeviceAction deviceAction, String text) {
        this.deviceAction = deviceAction;
        this.text = text;
    }

    public DeviceAction getDeviceAction() {
        return deviceAction;
    }

    public String getText() {
        return text;
    }
}
