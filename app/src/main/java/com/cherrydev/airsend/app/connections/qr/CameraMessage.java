package com.cherrydev.airsend.app.connections.qr;

import android.view.SurfaceHolder;

public class CameraMessage {

    private String message;
    private boolean kill;
    private CameraMessageType type;
    private SurfaceHolder holder;

    public CameraMessage(CameraMessageType start, SurfaceHolder holder) {
        this.type = start;
        this.holder = holder;
    }

    public CameraMessage(String message) {
        this.message = message;
    }

    public CameraMessage(boolean kill) {
        this.kill = kill;
    }

    public CameraMessageType getType() {
        return type;
    }

    public CameraMessage(CameraMessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public boolean isKill() {
        return kill;
    }

    public SurfaceHolder getHolder() {
        return holder;
    }
}