package com.cherrydev.airsend.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//https://stackoverflow.com/questions/38365325/android-alarmmanager-alarms-after-reboot-2016/38366564#38366564
public class BootReceiver extends BroadcastReceiver {
    public static final String TAG = "RestartAlarmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ServerService.startService();

            //ClientSSL.getInstance().connectToSaved();
        }
    }
}
