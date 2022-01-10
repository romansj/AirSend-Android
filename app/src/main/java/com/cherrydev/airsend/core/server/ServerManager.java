package com.cherrydev.airsend.core.server;


import com.cherrydev.airsend.app.utils.mymodels.ObservableSubject;
import com.cherrydev.airsend.core.ClientMessage;
import com.cherrydev.airsend.core.utils.RxHelper;
import com.cherrydev.common.MyResult;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import timber.log.Timber;

public class ServerManager {

    private static ServerManager INSTANCE;
    private ObservableSubject<ServerMessage> observableMessageEvent;

    public Observable<ServerMessage> getObservableMessageEvent() {
        return observableMessageEvent.getObservable();
    }


    public static ServerManager getInstance() {
        if (INSTANCE == null) INSTANCE = new ServerManager();
        return INSTANCE;
    }


    private ServerManager() {
    }


    //listen for incoming connections
    public Observable<ClientMessage> startServer(int port) {
        // find listener thread for that port
        // does not listen --> start listening (new listener thread)
        // listens already --> don't do anything

        //checking in case user manually provides port number -- so there is no attempt to listen on the same port
        ListenerThread runningServer = serverIsRunning(port); //check if a client with IP (specified in MyMessage message) is alive.
        //start client because we did not find a client with this IP
        if (runningServer != null) {
            Timber.d("server for " + port + " already running");
            return runningServer.getServerMessageCallback().getObservable();
        }
        Timber.d("server for " + port + " NOT yet running");


        ObservableSubject<ClientMessage> observableMessage = new ObservableSubject<>();
        observableMessageEvent = new ObservableSubject<>();

        ListenerThread connectionListenerThread = new ListenerThread(port, observableMessage, observableMessageEvent);
        connectionListenerThread.start();


        // use case disregarded -- listening on multiple ports -- abstractions in place for future
        threadInfoList.put(port, connectionListenerThread);


        return observableMessage.getObservable();
    }


    private ListenerThread serverIsRunning(int port) {
        for (Map.Entry<Integer, ListenerThread> entry : threadInfoList.entrySet()) {
            Timber.d("Key = " + entry.getKey() + ", Value = " + entry.getValue().getId() + ", connected " + entry.getValue().getServerThreadList().size());

            if (port == entry.getKey() && entry.getValue().isConnectionListenerRunning()) return entry.getValue();
        }

        return null;
    }


    public void stopServer() {
        RxHelper.submitToRx(() -> {
            threadInfoList.forEach((integer, listenerThreadInfo) -> {
                Timber.d("stopServer " + listenerThreadInfo.getPort());
                listenerThreadInfo.stopServer();
            });

            return MyResult.success();

        });

        //find server by port
        //if active, stop
    }


    //here instead of IP, will identify each thread by port its listening on
    private Map<Integer, ListenerThread> threadInfoList = new HashMap<>();
}
