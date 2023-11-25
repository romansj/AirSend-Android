package com.cherrydev.airsend.app.utils.permission;

public class Request {
    private RequestCode code;
    private String[] permissionsArray;

    public Request(RequestCode code, String[] permissionsArray) {
        this.code = code;
        this.permissionsArray = permissionsArray;
    }

    public int getCode() {
        return code.getCode();
    }

    public String[] getPermissions() {
        return permissionsArray;
    }
}
