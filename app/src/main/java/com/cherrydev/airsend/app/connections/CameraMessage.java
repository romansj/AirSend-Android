package com.cherrydev.airsend.app.connections;

import android.view.SurfaceHolder;

public class CameraMessage {

    private String message;
    private boolean kill;
    private Type type;
    private SurfaceHolder holder;

    public CameraMessage(Type start, SurfaceHolder holder) {
        this.type = start;
        this.holder = holder;
    }

    public CameraMessage(String message) {
        this.message = message;
    }

    public CameraMessage(boolean kill) {
        this.kill = kill;
    }

    public enum Type {
        START, PAUSE, STOP
    }

    public Type getType() {
        return type;
    }

    public CameraMessage(Type type) {
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