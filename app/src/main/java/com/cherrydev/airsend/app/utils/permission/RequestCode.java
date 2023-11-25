package com.cherrydev.airsend.app.utils.permission;

public enum RequestCode {
    REQUEST_CAMERA_PERMISSION(201);

    private final int code;

    RequestCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
