package com.cherrydev.airsend.core.server;

import com.cherrydev.airsend.core.ClientManagerOwnerProperties;
import com.cherrydev.airsend.app.utils.mymodels.ObservableSubject;
import com.cherrydev.airsend.core.ClientMessage;
import com.cherrydev.airsend.core.utils.SSLUtils;

import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import timber.log.Timber;

class ListenerThread extends Thread {

    private SSLServerSocket sslServerSocket;

    private List<ServerThread> serverThreadList;
    private int port;
    private ObservableSubject<ClientMessage> serverMessageCallback;
    private ObservableSubject<ServerMessage> serverEventCallback;
    private boolean connectionListenerRunning = true;
    private ClientManagerOwnerProperties ownerProperties;

    public ListenerThread(int port, ObservableSubject<ClientMessage> serverCallback, ObservableSubject<ServerMessage> serverEventCallback) {
        this.serverThreadList = new ArrayList<>();
        this.port = port;
        this.serverMessageCallback = serverCallback;
        this.serverEventCallback = serverEventCallback;
    }

    public void run() {
        Timber.d("Connection listener thread started running");
        SSLContext sslContext = SSLUtils.createSSLContext();

        try {
            // Create server socket factory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Create server socket
            //SSLServerSocket sslServerSocket;
            try {
                sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            } catch (BindException e) { //if cannot bind, then connection has lingered, need to pick another port
                sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(0);
            }

            int localPort = sslServerSocket.getLocalPort();
            Timber.d("listening on port: " + localPort);
            //serverMessageCallback.setValue(new ServerMessage(ServerOperation.LISTEN, localPort));
            serverEventCallback.setValue(new ServerMessage(ServerOperation.LISTEN, localPort));


            while (connectionListenerRunning) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                Timber.d("sslSocket accepted");

                ServerThread serverThread = new ServerThread(sslSocket, serverEventCallback, serverMessageCallback, localPort);
                serverThread.setOwnerProperties(ownerProperties);
                serverThread.start();
                serverThreadList.add(serverThread);
            }

            Timber.d("exited while:connectionListenerRunning loop");

        } catch (Exception ex) {
            ex.printStackTrace();

            stopServer();

            connectionListenerRunning = false;
        }
    }

    public List<ServerThread> getServerThreadList() {
        return serverThreadList;
    }

    public int getPort() {
        return port;
    }

    public ObservableSubject<ClientMessage> getServerMessageCallback() {
        return serverMessageCallback;
    }

    public boolean isConnectionListenerRunning() {
        return connectionListenerRunning;
    }

    public void stopServer() {
        Timber.d("ConnectionListenerThread::stopServer() called " + port);
        connectionListenerRunning = false;

        serverThreadList.forEach(serverThread -> serverThread.stopServer());


        try {
            sslServerSocket.close();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setOwnerProperties(ClientManagerOwnerProperties ownerProperties) {
        this.ownerProperties=ownerProperties;
    }
}
