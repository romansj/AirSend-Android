package com.cherrydev.airsend.core.client;

import com.cherrydev.airsend.core.Status;

import java.util.List;

/**
 * Interface that must be implemented by event handlers of ClientManager.
 * CM will call these methods if event handler is supplied.
 */
public interface IClientHandler {
    void updateClient(String ip, Status b, String textResponse);


    void removeClient(String ip);

    void deleteClients(List<String> list);

    void addClient(String ip, int port);

    void updateClients(List<String> listToUpdate, Status b);
}
