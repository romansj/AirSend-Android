package com.cherrydev.airsend.app.service.notification;

import android.app.PendingIntent;

public abstract class NotificationAction<T> {
    public abstract String getActionText();

    public abstract T getActionType();

    public abstract int getIcon();

    public abstract PendingIntent getPendingIntent();
}
