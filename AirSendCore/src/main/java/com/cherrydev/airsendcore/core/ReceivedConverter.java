package com.cherrydev.airsendcore.core;

import com.cherrydev.time.CommonTimeUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ReceivedConverter {
    public static ClientMessage fromReceived(String received, String ipAddress, int port) {
        MessageType messageType = typeFromReceived(received);
        String message = userMessageFromReceived(received, messageType);
        LocalDateTime datetimeNow = LocalDateTime.now();
        long milli = datetimeNow.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        ClientMessage clientMessage = new ClientMessage(ipAddress, port, received, message, messageType, milli);
        return clientMessage;
    }

    private static MessageType typeFromReceived(String received) {
        int indexOfConnect = received.lastIndexOf(Constants.OPEN);
        int indexOfDisconnect = received.lastIndexOf(Constants.CLOSE);
        int indexOfEnd = received.lastIndexOf(Constants.EOF);

        if (indexOfConnect != -1) return MessageType.CONNECT;
        if (indexOfDisconnect != -1) return MessageType.DISCONNECT;
        if (indexOfEnd != -1) return MessageType.MESSAGE;

        return MessageType.MESSAGE;
    }

    private static String userMessageFromReceived(String received, MessageType messageType) {
        int indexEOF = getTerminatorIndex(received, messageType);
        String resultMessage = received.substring(0, indexEOF).trim();

        return resultMessage;
    }

    private static int getTerminatorIndex(String received, MessageType messageType) {
        int indexEOF = -1;
        switch (messageType) {
            case CONNECT:
                indexEOF = received.lastIndexOf(Constants.OPEN);
                break;

            case DISCONNECT:
                indexEOF = received.lastIndexOf(Constants.CLOSE);
                break;

            default:
            case MESSAGE:
                indexEOF = received.lastIndexOf(Constants.EOF);
                break;
        }

        return indexEOF;
    }
}
