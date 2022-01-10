package com.cherrydev.airsend.core.server;

import com.cherrydev.airsend.app.utils.mymodels.ObservableSubject;
import com.cherrydev.airsend.core.ClientMessage;
import com.cherrydev.airsend.core.Constants;
import com.cherrydev.airsend.core.ReceivedConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import timber.log.Timber;


// Thread handling the socket from client
public class ServerThread extends Thread {
    private final int port;
    private SSLSocket sslSocket;
    private ObservableSubject<ServerMessage> serverCallback;
    private ObservableSubject<ClientMessage> serverMessageCallback;
    private boolean serverRunning;

    ServerThread(SSLSocket sslSocket, ObservableSubject<ServerMessage> serverCallback, ObservableSubject<ClientMessage> serverMessageCallback, int port) {
        this.sslSocket = sslSocket;
        this.serverCallback = serverCallback;
        this.port = port;
        this.serverMessageCallback = serverMessageCallback;
    }


    public void run() {
        if (serverRunning) {
            Timber.d("already running");
            return;
        }

        Timber.d("not running. Starting server");


        serverCallback.setValue(new ServerMessage(ServerOperation.START, sslSocket.getPort()));
        serverRunning = true;


        Timber.d("ServerThread started");
        serverRunning = true;

        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());

        try {
            //commented out because any call to either startHandshake or creating inputstream will start the handshake
            // Start handshake
            //sslSocket.startHandshake();

            // Get session after the connection is established
            SSLSession sslSession = sslSocket.getSession();

            Timber.d("SSLSession:" + " Protocol: " + sslSession.getProtocol() + ", Cipher suite : " + sslSession.getCipherSuite());

            // Start handling application content
            InputStream inputStream = sslSocket.getInputStream();
            OutputStream outputStream = sslSocket.getOutputStream();


            // in C# here would do inputstream.read(1,2,3)
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));


            while (serverRunning) {
                Timber.d("run: while (true)");
                StringBuilder builder = new StringBuilder();

                int read;
                while ((read = bufferedReader.read()) != -1) {

                    char c = (char) read;
                    builder.append(c);

                    if (builder.toString().contains(Constants.EOF)) break;
                }

                if (read == -1) {
                    break;
                }


                String mssg = builder.toString();

                printWriter.print("HTTP/1.1 200\r\n");
                printWriter.flush();


                //String mssg = builder.toString();
                Timber.d("textToShow " + mssg.trim());
                //if (!mssg.trim().isEmpty()) {
                ClientMessage reconstructedMessage = ReceivedConverter.fromReceived(mssg, sslSocket.getInetAddress().toString().substring(1), port);
                serverMessageCallback.setValue(reconstructedMessage);
                //}

                // if received close, then need to end connection (after ack and callback are complete)
                int lastIndexOfClose = mssg.lastIndexOf(Constants.CLOSE);
                if (lastIndexOfClose != -1) {
                    bufferedReader.close();
                    printWriter.close();

                    stop(new InterruptedException("close received"));
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            stop(ex);
        }
    }


    public void stopServer() {
        stop(new InterruptedException("Stop called"));
    }


    private void stop(Exception e) {
        Timber.d("stop1 called, e: " + e);
        serverRunning = false;


        Timber.d("attempting to close sslsocket");
        try {
            sslSocket.close();
            Timber.d("sslsocket closed");
        } catch (NullPointerException | IOException ex) {
            ex.printStackTrace();
        }


        serverCallback.setValue(new ServerMessage(ServerOperation.STOP, port));
    }
}