package com.cherrydev.airsend.app.database.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.cherrydev.airsend.app.messages.IMessage;

import java.time.LocalDateTime;
import java.time.ZoneId;

import io.github.romansj.core.ClientMessage;
import io.github.romansj.core.MessageType;
import io.github.romansj.core.SentStatus;

@Entity
public class SentMessage implements IMessage {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String IP;
    private int port;
    private String transferMessage;
    private String userMessage;

    private MessageType type;
    private long dateTime;
    private int retryCount = 0;

    private SentStatus sentStatus = SentStatus.SENT;


    public SentMessage() {

    }


    public SentMessage(ClientMessage message) {
        this.IP = message.getIP();
        this.port = message.getPort();
        this.transferMessage = message.getMessage();
        this.userMessage = message.getUserMessage();
        this.type = message.getType();

        // TODO assumption, at time when ClientMessage should be the one to always have date?
        this.dateTime = message.getDateTime() == -1 ?
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                :
                message.getDateTime();

        this.retryCount = message.getRetryCount();
    }

    public String getIP() {
        return IP;
    }

    @Override
    public String getText() {
        return getUserMessage();
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTransferMessage() {
        return transferMessage;
    }

    public void setTransferMessage(String transferMessage) {
        this.transferMessage = transferMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public long getDateTime() {
        return dateTime;
    }

    @Override
    public String getStatus() {
        return sentStatus.name() +
                (retryCount == 0 ? "" : "  |  RETRIES: " + retryCount);
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public SentStatus getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(SentStatus sentStatus) {
        this.sentStatus = sentStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}