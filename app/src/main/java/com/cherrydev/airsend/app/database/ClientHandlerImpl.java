package com.cherrydev.airsend.app.database;

import static com.cherrydev.airsend.app.MyApplication.databaseManager;

import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsend.core.Status;
import com.cherrydev.airsend.core.client.IClientHandler;

import java.util.List;

public class ClientHandlerImpl implements IClientHandler {
    @Override
    public void updateClient(String ip, Status b) {
        databaseManager.updateDevice(ip, b).runInBackground().run();
    }

    @Override
    public void updateClients(List<String> list, Status b) {
        databaseManager.updateDevices(list, b).runInBackground().run();
    }

    @Override
    public void removeClient(String ip) {
        databaseManager.deleteDevice(ip).runInBackground().run();
    }

    @Override
    public void deleteClients(List<String> list) {
        databaseManager.deleteDevices(list).runInBackground().run();
    }

    @Override
    public void addClient(String ip, int port) {
        databaseManager.addDevice(new Device("Device name", ip, port)).runInBackground().run();
    }


}
