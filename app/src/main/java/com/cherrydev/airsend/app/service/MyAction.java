package com.cherrydev.airsend.app.service;

import android.app.PendingIntent;

import com.cherrydev.airsend.app.service.notification.NotificationAction;
import com.cherrydev.airsend.app.utils.IntentAction;

class MyAction extends NotificationAction<IntentAction> {

    private String actionText;
    private IntentAction intentAction;
    private int drawable;
    private PendingIntent pendingIntent;

    public MyAction(IntentAction intentAction, int drawable, PendingIntent pendingIntent) {
        this.actionText = intentAction.getDisplayName();
        this.intentAction = intentAction;
        this.drawable = drawable;
        this.pendingIntent = pendingIntent;
    }

    @Override
    public String getActionText() {
        return actionText;
    }

    @Override
    public IntentAction getActionType() {
        return intentAction;
    }

    @Override
    public int getIcon() {
        return drawable;
    }

    @Override
    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }
}
