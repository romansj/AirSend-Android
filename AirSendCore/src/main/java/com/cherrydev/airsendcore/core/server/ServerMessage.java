package com.cherrydev.airsendcore.core.server;

public class ServerMessage {
    private ServerOperation operation;
    private int port;

    public ServerOperation getOperation() {
        return operation;
    }

    public int getPort() {
        return port;
    }

    public ServerMessage(ServerOperation operation, int port) {
        this.operation = operation;
        this.port = port;
    }
}
