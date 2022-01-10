package com.cherrydev.airsend.app.database;

import android.content.Context;

import androidx.room.Room;

import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsend.app.database.models.UserMessage;
import com.cherrydev.airsend.core.Status;

import java.util.List;

public class DatabaseManager {
    private static AppDatabase DB;

    AppDatabase getDatabase(Context context) {
        if (DB == null) {
            DB = Room.databaseBuilder(context, AppDatabase.class, "database-name")
                    .fallbackToDestructiveMigration()  //todo destructive
                    .build();
        }

        return DB;
    }

    public DatabaseManager(Context context) {
        getDatabase(context);
    }

    public DatabaseManager(AppDatabase db) {
        DB = db;
    }


    public DbDao getDb() {
        return DB.dao();
    }


    public Device findDeviceByIP(String ip) {
        var byIP = getDb().findByIP(ip);
        return byIP;
    }


    public DatabaseQuery.Builder addMessage(UserMessage serverMessage) {
        return new DatabaseQuery.Builder().setQuery(() -> getDb().addMessage(serverMessage));
    }


    public DatabaseQuery.Builder addDevice(Device device) {
        return new DatabaseQuery.Builder().setQuery(() -> addDeviceInternal(device));
    }


    private void addDeviceInternal(Device deviceNew) {
        Device deviceExisting = getDb().findByIP(deviceNew.getIP());

        if (deviceExisting != null) {
            Device.updateExistingDevice(deviceExisting, deviceNew);
            getDb().updateDevice(deviceExisting);

        } else {
            if (Device.createNew(deviceNew)) getDb().addDevice(deviceNew);
        }
    }


    public DatabaseQuery.Builder updateDevices(List<String> ip, Status isRunning) {
        return new DatabaseQuery.Builder().setQuery(() -> getDb().updateDevices(ip, isRunning)); //running client - message does not say to kill
    }


    public DatabaseQuery.Builder updateDevice(String ip, Status isRunning) {
        return new DatabaseQuery.Builder().setQuery(() -> getDb().updateDevice(ip, isRunning)); //running client - message does not say to kill
    }


    public DatabaseQuery.Builder deleteDevice(String ip) {
        return new DatabaseQuery.Builder().setQuery(() -> getDb().deleteDevice(ip));
    }

    public DatabaseQuery.Builder deleteDevices(List<String> list) {
        return new DatabaseQuery.Builder().setQuery(() -> getDb().deleteDevices(list)); //running client - message does not say to kill
    }


    public DatabaseQuery.Builder nukeAll() {
        return new DatabaseQuery.Builder().setQuery(() -> {
            getDb().deleteAllDevices();
            getDb().deleteAllMessages();
        });
    }

    public DatabaseQuery.Builder deleteMessage(long id) {
        return new DatabaseQuery.Builder().setQuery(() -> getDb().deleteMessage(id));
    }


    public DatabaseQuery.Builder deleteAllMessages() {
        return new DatabaseQuery.Builder().setQuery(() -> getDb().deleteAllMessages());
    }
}
