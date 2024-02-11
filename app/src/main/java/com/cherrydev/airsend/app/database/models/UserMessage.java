package com.cherrydev.airsend.app.database.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.cherrydev.airsend.app.messages.IMessage;

import io.github.romansj.core.message.MessageType;


@Entity
public class UserMessage implements IMessage {


    @PrimaryKey(autoGenerate = true)
    private long id;

    private String IP = "";
    private int port;
    private String text;
    private MessageType type;
    private long dateTime;


    public UserMessage() {
    }

    public UserMessage(String ip, int port, String userMessage, MessageType type, long dateTime) {
        this.IP = ip;
        this.port = port;
        this.text = userMessage;
        this.type = type;
        this.dateTime = dateTime;
    }

    public String getIP() {
        return IP;
    }

    public String getText() {
        return text;
    }

    public MessageType getType() {
        return type;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getDateTime() {
        return dateTime;
    }

    @Override
    public String getStatus() {
        return null;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
