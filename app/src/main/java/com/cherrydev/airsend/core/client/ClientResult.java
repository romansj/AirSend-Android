package com.cherrydev.airsend.core.client;

public class ClientResult {
    private boolean clientRunning;
    private Throwable throwable;

    public ClientResult(boolean clientRunning, Throwable throwable) {
        this.clientRunning = clientRunning;
        this.throwable = throwable;
    }

    public ClientResult(boolean clientRunning) {
        this(clientRunning, null);
    }

    public boolean isClientRunning() {
        return clientRunning;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "ClientResult{" +
                "clientRunning=" + clientRunning +
                ", throwable=" + throwable +
                '}';
    }
}
