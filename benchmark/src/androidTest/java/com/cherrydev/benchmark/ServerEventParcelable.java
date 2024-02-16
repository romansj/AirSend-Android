package com.cherrydev.benchmark;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.github.romansj.core.server.ServerOperation;

public class ServerEventParcelable implements Parcelable {
    private ServerOperation operation;
    private int port;

    public ServerOperation getOperation() {
        return operation;
    }

    public int getPort() {
        return port;
    }

    public ServerEventParcelable(ServerOperation operation, int port) {
        this.operation = operation;
        this.port = port;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.JSON_STYLE).toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(operation.name());
        dest.writeInt(port);
    }
}
