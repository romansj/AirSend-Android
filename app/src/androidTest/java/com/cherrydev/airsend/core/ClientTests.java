package com.cherrydev.airsend.core;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.cherrydev.airsend.app.database.ClientHandlerImpl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


import io.github.romansj.core.Status;
import io.github.romansj.core.client.ClientManager;
import io.github.romansj.core.message.DeviceProperties;
import io.github.romansj.core.utils.CoreNetworkUtils;
import io.github.romansj.core.utils.SSLUtils;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;


@RunWith(AndroidJUnit4.class)
public class ClientTests {
    static int port;
    static String ip;

    @BeforeClass
    public static void setupClass() {
        // this allows us to execute code on the calling thread so the test case would not exit before we are done
        RxJavaPlugins.setIoSchedulerHandler(__ -> Schedulers.trampoline());


        port = CoreNetworkUtils.nextFreePort(30000, 50000);
        ip = CoreNetworkUtils.getIPAddress(false);
    }

    @Test
    public void useAppContext() {
        // if we don't pass here, then test did not instantiate correctly
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.cherrydev.airsend", appContext.getPackageName());
    }


    @Test
    public void connectNewCallsAdd() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        ClientManager clientManager = getClientManager();


        var clientHandler = mock(ClientHandlerImpl.class);
        clientManager.setClientHandler(clientHandler);


        clientManager.connect(ip, port);
        verify(clientHandler).addClient(ip, port);
    }


    @Test
    public void clientRetriesOnFail() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        ClientManager clientManager = getClientManager();


        var clientHandler = mock(ClientHandlerImpl.class);
        clientManager.setClientHandler(clientHandler);


        clientManager.messageClient(ip, port, "Hello");
        verify(clientHandler, after(500)
                .atMost(1 + clientManager.getMaxRetries()))
                .addClient(ip, port); //1 attempt + 3 re-attempts
    }

    @Test
    public void retriesOnMessageAfterFailToConnectRetry() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        ClientManager clientManager = getClientManager();


        var clientHandler = mock(ClientHandlerImpl.class);
        var inOrder = inOrder(clientHandler);

        clientManager.setClientHandler(clientHandler);


        clientManager.connect(ip, port);
        verify(clientHandler, after(500)
                .atMost(1 + clientManager.getMaxRetries()))
                .addClient(ip, port);

        clientManager.messageClient(ip, port, "Hello");
        verify(clientHandler, after(500)
                .atMost(2))
                .updateClient(ip, Status.NOT_RUNNING, null);
        // 2 : first for connect() and second for message() -- because verify() will capture both
    }


    @Test
    public void disconnectDoesNotRetry() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        ClientManager clientManager = getClientManager();

        var clientHandler = mock(ClientHandlerImpl.class);
        clientManager.setClientHandler(clientHandler);

        clientManager.disconnect(ip, port, false);
        verify(clientHandler, after(11000)).updateClient(ip, Status.NOT_RUNNING, null);
    }

    @NonNull
    private ClientManager getClientManager() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        var clientManager = ClientManager.getInstance();

        // clientManager.setSslContext(SSLUtils.createSSLContext("BKS", MyApplication.getInstance().getResources().openRawResource(R.raw.cherrydev), BuildConfig.CERT_KEY.toCharArray()));
        clientManager.setSslSocketFactory(SSLUtils.getSSLSocketFactory("test", "test123"));

        var ownerProperties = new DeviceProperties(port, "Instrumentation Runner", "Process");
        clientManager.setOwnerProperties(ownerProperties);

        return clientManager;
    }


}
