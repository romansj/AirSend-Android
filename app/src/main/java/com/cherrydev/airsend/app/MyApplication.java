package com.cherrydev.airsend.app;

import android.app.Application;
import android.os.StrictMode;

import com.cherrydev.airsend.BuildConfig;
import com.cherrydev.airsend.app.database.DatabaseManager;
import com.cherrydev.airsend.app.service.notification.NotificationUtils;
import com.cherrydev.airsend.core.Constants;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.conscrypt.Conscrypt;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.net.ssl.SSLContext;

import timber.log.Timber;

public class MyApplication extends Application {
    private static MyApplication INSTANCE;

    public static DatabaseManager databaseManager;


    public static MyApplication getInstance() {
        if (INSTANCE == null) INSTANCE = new MyApplication();
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        //to enable TLSv1.3
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
        System.setProperty("javax.net.debug", "ssl");


        toggleStrictMode(false);


        databaseManager = new DatabaseManager(getInstance());


        Timber.plant(new Timber.DebugTree());


        NotificationUtils.initNotificationChannels();


        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance(Constants.protocol);
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

    }

    private void toggleStrictMode(boolean enable) {
        if (!enable) return;

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build());

        if (BuildConfig.DEBUG) StrictMode.enableDefaults();
    }


}
