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
import com.cherrydev.airsend.app.service.ClientManagerOwnerProperties;
import com.cherrydev.airsend.app.utils.NetworkUtils;
import com.cherrydev.airsend.core.client.ClientManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

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


        port = NetworkUtils.nextFreePort(30000, 50000);
        ip = NetworkUtils.getIPAddress(false);
    }

    @Test
    public void useAppContext() {
        // if we don't pass here, then test did not instantiate correctly
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.cherrydev.airsend", appContext.getPackageName());
    }


    @Test
    public void connectNewCallsAdd() {
        ClientManager clientManager = getClientManager();


        var clientHandler = mock(ClientHandlerImpl.class);
        clientManager.setClientHandler(clientHandler);


        clientManager.connect(ip, port);
        verify(clientHandler).addClient(ip, port);
    }


    @Test
    public void clientRetriesOnFail() {
        ClientManager clientManager = getClientManager();


        var clientHandler = mock(ClientHandlerImpl.class);
        clientManager.setClientHandler(clientHandler);


        clientManager.messageClient(ip, port, "Hello");
        verify(clientHandler, after(500)
                .atMost(1 + clientManager.getMaxRetries()))
                .addClient(ip, port); //1 attempt + 3 re-attempts
    }

    @Test
    public void retriesOnMessageAfterFailToConnectRetry() {
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
                .updateClient(ip, Status.NOT_RUNNING);
        // 2 : first for connect() and second for message() -- because verify() will capture both
    }


    @Test
    public void disconnectDoesNotRetry() {
        ClientManager clientManager = getClientManager();


        var clientHandler = mock(ClientHandlerImpl.class);
        var inOrder = inOrder(clientHandler);

        clientManager.setClientHandler(clientHandler);


        clientManager.disconnect(ip, port);
        //verify(clientHandler, after(500).atMost(4)).addClient(ip, port);
        verify(clientHandler, after(500)).updateClient(ip, Status.NOT_RUNNING);
    }

    @NonNull
    private ClientManager getClientManager() {
        var ownerProperties = new ClientManagerOwnerProperties(port, "Instrumentation Runner", "Process");
        var clientManager = ClientManager.getInstance();
        clientManager.setOwnerProperties(ownerProperties);
        return clientManager;
    }


}
