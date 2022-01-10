package com.cherrydev.airsend.core;

import androidx.annotation.Nullable;

public class ClientMessage {

    private String IP;
    private int port;
    private String transferMessage;
    private String userMessage;

    private MessageType type;
    private String dateTime;
    private int retryCount = 0;

    private boolean awaitResponse = true;


    public ClientMessage(String IP, int port, MessageType type) {
        this(IP, port, null, type, null);
    }

    public ClientMessage(String IP, int port, String userMessage) {
        this(IP, port, userMessage, MessageType.MESSAGE, null);
    }

    public ClientMessage(String IP, int port, String userMessage, MessageType type, String dateTime) {
        this(IP, port, null, userMessage, type, dateTime);
    }

    public ClientMessage(String IP, int port, String ownerProperties, MessageType type) {
        this(IP, port, null, ownerProperties, type, null);
    }

    public ClientMessage(String IP, int port, String transferMessage, String userMessage, MessageType type, String dateTime) {
        this.IP = IP;
        this.port = port;
        this.transferMessage = transferMessage == null ? appendTerminator(type, userMessage) : transferMessage; // null => create from mssg, not null => use given
        this.userMessage = userMessage;
        this.type = type;
        this.dateTime = dateTime;
    }


    private String appendTerminator(MessageType type, String message) {
        if (message == null) message = "";
        String newMessage = message.trim();

        switch (type) {
            case MESSAGE:
                newMessage += Constants.EOF;
                break;
            case CONNECT:
                newMessage += Constants.OPEN;
                break;
            case DISCONNECT:
                newMessage += Constants.CLOSE;
                break;
        }

        return newMessage;
    }


    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public String getMessage() {
        return transferMessage;
    }

    public boolean isKill() {
        return type == MessageType.DISCONNECT;
    }


    public String getDateTime() {
        return dateTime;
    }


    public MessageType getType() {
        return type;
    }

    public boolean isConnectionType() {
        return type == MessageType.CONNECT || type == MessageType.DISCONNECT;
    }

    public String getUserMessage() {
        return userMessage;
    }


    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isAwaitResponse() {
        return awaitResponse;
    }

    public void setAwaitResponse(boolean awaitResponse) {
        this.awaitResponse = awaitResponse;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final ClientMessage other = (ClientMessage) obj;
        return this.transferMessage.equals(other.transferMessage) && this.IP.equals(other.IP) && this.type == other.type;

    }


}