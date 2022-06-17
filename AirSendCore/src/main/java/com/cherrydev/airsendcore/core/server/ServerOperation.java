package com.cherrydev.airsendcore.core.server;

import org.jetbrains.annotations.NotNull;

public enum ServerOperation {
    START("Started"),
    STOP("Stopped"),
    LISTEN("Started listening on port");


    private final String s;

    ServerOperation(String s) {
        this.s = s;
    }

    @NotNull
    @Override
    public String toString() {
        return s;
    }
}
