package com.cherrydev.airsend.app.utils;

public enum IntentAction {
    ACTION_COPY_TO_CLIPBOARD("ACTION_COPY_TO_CLIPBOARD"),
    ACTION_STOP_SERVICE("ACTION_STOP_SERVICE", "Stop"),
    ACTION_SEND("ACTION_SEND", "Send"), //android share menu action
    ACTION_SHARE_CLIPBOARD("ACTION_SHARE_CLIPBOARD", "Send"),
    ACTION_OPEN_APP("ACTION_OPEN_APP");

    private final String action;
    private final String displayName;

    IntentAction(String action) {
        this.action = action;
        this.displayName = action;
    }

    IntentAction(String action, String displayName) {
        this.action = action;
        this.displayName = displayName;
    }

    public String getAction() {
        return action;
    }

    public String getDisplayName() {
        return displayName;
    }
}
