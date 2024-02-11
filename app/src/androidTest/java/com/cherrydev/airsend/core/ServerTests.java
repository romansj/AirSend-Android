package com.cherrydev.airsend.core;


import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import io.github.romansj.core.message.Message;
import io.github.romansj.core.server.ServerManager;

import io.github.romansj.core.utils.CoreNetworkUtils;
import io.github.romansj.core.utils.SSLUtils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class ServerTests {
    static int port;
    static String ip;

    @BeforeClass
    public static void setupClass() {
        // this allows us to execute code on the calling thread so the test case would not exit before we are done
        RxJavaPlugins.setIoSchedulerHandler(__ -> Schedulers.trampoline());


        port = CoreNetworkUtils.nextFreePort(30000, 50000);
        ip = CoreNetworkUtils.getIPAddress(true);
    }

    // if we don't pass here, then test did not instantiate correctly
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.cherrydev.airsend", appContext.getPackageName());
    }


    @Test
    public void runningServerDoesNotRunTwice() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        ServerManager serverManager = getServerManager();

        Observable<Message> messageObservableStart = serverManager.startServer(port);
        Observable<Message> messageObservableStartAgain = serverManager.startServer(port);

        // start a started server should just return its observable
        assertThat(messageObservableStartAgain).isSameInstanceAs(messageObservableStart);
    }

    @Test
    public void restartedServerReturnsNewObservable() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        ServerManager serverManager = getServerManager();

        Observable<Message> messageObservableStart = serverManager.startServer(port);
        serverManager.stopServer();
        Observable<Message> messageObservableStartAgain = serverManager.startServer(port);

        // two diff instances unless cleanup was skipped
        assertThat(messageObservableStartAgain).isNotSameInstanceAs(messageObservableStart);
    }

    private ServerManager getServerManager() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        ServerManager serverManager = ServerManager.getInstance();
        // serverManager.setSslContext(SSLUtils.createSSLContext("BKS", MyApplication.getInstance().getResources().openRawResource(R.raw.cherrydev), BuildConfig.CERT_KEY.toCharArray()));
        serverManager.setSSLServerSocketFactory(SSLUtils.getSSLServerSocketFactory("test", "test123"));
        return serverManager;
    }


}
