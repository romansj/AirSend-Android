package com.cherrydev.airsend.app.messages;

import static com.cherrydev.airsend.app.MyApplication.databaseManager;

import androidx.annotation.Nullable;

import com.cherrydev.airsend.app.database.models.SentMessage;
import com.cherrydev.airsendcore.core.ClientMessage;
import com.cherrydev.airsendcore.core.SentStatus;
import com.cherrydev.airsendcore.core.client.ClientResult;
import com.cherrydev.airsendcore.core.client.ISentMessageHandler;

public class SentMessageHandlerImpl implements ISentMessageHandler {
    @Override
    public void updateMessageStatus(ClientMessage message, ClientResult clientResult) {
        SentMessage sentMessage = getAndAddSentMessage(message);
        if (sentMessage == null) return;

        var sentStatus = clientResult.isClientRunning() ? SentStatus.RECEIVED : SentStatus.FAILED;
        sentMessage.setSentStatus(sentStatus);
        databaseManager.getDb().updateSentMessage(sentMessage);
    }


    @Override
    public void updateMessageStatus(ClientMessage message, Throwable throwable) {
        SentMessage sentMessage = getAndAddSentMessage(message);
        if (sentMessage == null) return;

        var sentStatus = SentStatus.FAILED;
        sentMessage.setSentStatus(sentStatus);
        databaseManager.getDb().updateSentMessage(sentMessage);
    }

    @Nullable
    private SentMessage getAndAddSentMessage(ClientMessage message) {
        var sentMessage = databaseManager.getDb().findSentMessage(message.getIdentifier());
        if (sentMessage == null) {
            sentMessage = new SentMessage(message);
            databaseManager.addSentMessage(sentMessage);
            return null;
        }
        return sentMessage;
    }


    @Override
    public long addSentMessage(ClientMessage message) {
        var sentMessage = new SentMessage(message);
        return databaseManager.addSentMessage(sentMessage); // TODO within return QueryBuilder run() convention, how to return some value?
    }
}
