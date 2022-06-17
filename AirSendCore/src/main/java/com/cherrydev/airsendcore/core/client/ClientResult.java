package com.cherrydev.airsendcore.core.client;

public class ClientResult {
    private boolean clientRunning;
    private Throwable throwable;
    private String textResponse;

    public ClientResult(boolean clientRunning, Throwable throwable) {
        this.clientRunning = clientRunning;
        this.throwable = throwable;
    }

    public ClientResult(boolean clientRunning, String textResponse) {
        this.clientRunning = clientRunning;
        this.textResponse = textResponse;
    }

    public ClientResult(boolean clientRunning) {
        this(clientRunning, "");
    }

    public boolean isClientRunning() {
        return clientRunning;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getTextResponse() {
        return textResponse;
    }

    @Override
    public String toString() {
        return "ClientResult{" +
                "clientRunning=" + clientRunning +
                ", throwable=" + throwable +
                ", textResp=" + textResponse +
                '}';
    }
}
