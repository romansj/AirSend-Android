package com.cherrydev.airsend.app;

import android.app.Application;
import android.os.StrictMode;

import com.cherrydev.airsend.BuildConfig;
import com.cherrydev.airsend.app.database.DatabaseManager;
import com.cherrydev.airsend.app.service.ServerService;
import com.cherrydev.airsend.app.service.notification.NotificationUtils;
import com.cherrydev.airsend.app.settings.PreferenceKey;
import com.cherrydev.airsend.app.settings.PreferenceUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.conscrypt.Conscrypt;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.net.ssl.SSLContext;

import io.github.romansj.core.message.DeviceProperties;
import timber.log.Timber;

public class MyApplication extends Application {
    private static MyApplication INSTANCE;

    public static DatabaseManager databaseManager;


    public static MyApplication getInstance() {
        if (INSTANCE == null) INSTANCE = new MyApplication();
        return INSTANCE;
    }

    public static DeviceProperties getOwnerProperties() {
        var settingDeviceName = PreferenceUtils.getString(PreferenceKey.DEVICE_NAME);
        return new DeviceProperties(ServerService.getPORT(), settingDeviceName, "A");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;


        Security.insertProviderAt(new BouncyCastleProvider(), 1); // enable BC security implementation
        Security.insertProviderAt(Conscrypt.newProvider(), 2); // enable TLSv1.3
        System.setProperty("javax.net.debug", "ssl");


        toggleStrictMode(false);


        databaseManager = new DatabaseManager(getInstance());


        Timber.plant(new Timber.DebugTree());


        NotificationUtils.initNotificationChannels();


        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException | NoSuchAlgorithmException | KeyManagementException e) {
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

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
