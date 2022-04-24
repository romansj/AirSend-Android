package com.cherrydev.airsend.core.client;

import com.cherrydev.airsend.app.utils.mymodels.ObservableSubject;
import com.cherrydev.airsend.core.ClientMessage;
import com.cherrydev.airsend.core.utils.SSLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import timber.log.Timber;

public class ClientThread extends Thread {

    public static final String HTTP_200 = "HTTP/1.1 200";
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private final String IP;
    private final int port;
    private boolean clientIsRunning = false;
    private final ObservableSubject<ClientMessage> clientObservableIn;
    private final PublishSubject<ClientResult> clientObservableOut;
    private int errorCount = 0;


    public ClientThread(String IP, int PORT, ObservableSubject<ClientMessage> observableMessage, PublishSubject<ClientResult> observableMessageOut) {
        this.IP = IP;
        this.port = PORT;
        this.clientObservableIn = observableMessage;
        this.clientObservableOut = observableMessageOut;
    }


    public void run() {
        clientIsRunning = true;

        try {

            SSLContext sslContext = SSLUtils.createSSLContext();
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(IP, port);
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());

            // A call to read from socket or explicit startHandshake (doesn't matter which you use) will start the handshake.
            sslSocket.startHandshake();

            // after the connection is established, gets session
            SSLSession sslSession = sslSocket.getSession();

            Timber.d("SSLSession:" + " Protocol: " + sslSession.getProtocol() + ", Cipher suite : " + sslSession.getCipherSuite());
            // clientObservableOut.onNext(new ClientResult(true));

            // Start handling application content
            InputStream inputStream = sslSocket.getInputStream();
            OutputStream outputStream = sslSocket.getOutputStream();


            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            printWriter = new PrintWriter(new OutputStreamWriter(outputStream));


            clientObservableIn.getObservable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(message -> {
                handleMessage(sslSocket, message);
            }, error -> {
                error.printStackTrace();
                clientIsRunning = false;
                clientObservableOut.onNext(new ClientResult(false, error));
            });

            //sslSocket.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            clientIsRunning = false;
            clientObservableOut.onNext(new ClientResult(false, ex));
        }
    }

    private void handleMessage(SSLSocket sslSocket, ClientMessage message) throws IOException {
        Timber.d("client receiving new mssg " + message.getMessage() + ", socket info " + SSLUtils.getSocketInfo(sslSocket));

        printWriter.println(message.getMessage());
        printWriter.flush();


        if (message.isAwaitResponse()) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) { // reading response after sending our message, blocking until null
                Timber.d("Input : " + line);

                if (line.trim().startsWith(HTTP_200)) { // TODO what *should* we do if it's not 200?
                    Timber.d("Mssg equals HTTP 200");
                    line = line.replace(HTTP_200, ""); // don't need to forward response code for processing, only remainder
                    clientObservableOut.onNext(new ClientResult(true, line)); // if server does not respond, a timeout exception is raised and receiving server is assumed not to be running
                    break;
                }
            }
        }


        if (message.isKill()) {
            clientIsRunning = false;
            clientObservableOut.onNext(new ClientResult(false));
            sslSocket.close();
        }
    }

    public PublishSubject<ClientResult> getClientObservableOut() {
        return clientObservableOut;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public boolean isClientRunning() {
        return clientIsRunning;
    }

    public void sendMessage(ClientMessage message) {
        clientObservableIn.setValue(message);
    }


    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        errorCount = errorCount;
    }
}
