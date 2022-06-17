package com.cherrydev.airsendcore.core.client;

import com.cherrydev.airsendcore.core.ClientMessage;

public interface ISentMessageHandler {
    void updateMessageStatus(ClientMessage message, ClientResult clientResult);

    void updateMessageStatus(ClientMessage message, Throwable clientResult);

    long addSentMessage(ClientMessage message);
}
