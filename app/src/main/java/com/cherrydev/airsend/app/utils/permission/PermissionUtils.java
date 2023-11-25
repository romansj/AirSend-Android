package com.cherrydev.airsend.app.utils.permission;

import android.Manifest;
import android.content.pm.PackageManager;

public class PermissionUtils {
    public static final Request cameraRequest = new Request(RequestCode.REQUEST_CAMERA_PERMISSION, new String[]{Manifest.permission.CAMERA});

    public static boolean permissionGranted(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
