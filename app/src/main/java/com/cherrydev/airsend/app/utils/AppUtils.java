package com.cherrydev.airsend.app.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsend.app.service.ServerService;
import com.cherrydev.airsend.app.service.notification.NotificationUtils;

public class AppUtils {
    public static void restartApp() {
        cleanUp();

        PackageManager packageManager = MyApplication.getInstance().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(MyApplication.getInstance().getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        MyApplication.getInstance().startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    private static void cleanUp() {
        ServerService.stopService();
        NotificationUtils.dismissNotifications(); //after server stopped in case any final notifications are displayed
    }

    public static void killApp() {
        cleanUp();
        Runtime.getRuntime().exit(0);
    }
}
