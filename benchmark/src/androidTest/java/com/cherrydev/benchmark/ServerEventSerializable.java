package com.cherrydev.benchmark;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

import io.github.romansj.core.server.ServerOperation;

public class ServerEventSerializable implements Serializable {
    private ServerOperation operation;
    private int port;

    public ServerOperation getOperation() {
        return operation;
    }

    public int getPort() {
        return port;
    }

    public ServerEventSerializable(ServerOperation operation, int port) {
        this.operation = operation;
        this.port = port;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.JSON_STYLE).toString();
    }
}
