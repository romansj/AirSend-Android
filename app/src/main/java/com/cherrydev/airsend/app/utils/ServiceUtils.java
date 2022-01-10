package com.cherrydev.airsend.app.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.cherrydev.airsend.app.MyApplication;

public class ServiceUtils {
    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MyApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
