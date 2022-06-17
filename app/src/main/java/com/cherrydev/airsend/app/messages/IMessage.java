package com.cherrydev.airsend.app.messages;

public interface IMessage {
    long getId();

    String getIP();

    String getText();

    long getDateTime();

    String getStatus();
}
