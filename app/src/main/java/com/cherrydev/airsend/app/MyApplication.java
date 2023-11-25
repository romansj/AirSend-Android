package com.cherrydev.airsend.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;

import com.cherrydev.airsend.BuildConfig;
import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.database.DatabaseManager;
import com.cherrydev.airsend.app.service.ServerService;
import com.cherrydev.airsend.app.service.notification.NotificationUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.conscrypt.Conscrypt;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.net.ssl.SSLContext;

import io.github.romansj.core.Constants;
import io.github.romansj.core.OwnerProperties;
import timber.log.Timber;

public class MyApplication extends Application {
    private static MyApplication INSTANCE;

    public static DatabaseManager databaseManager;


    public static MyApplication getInstance() {
        if (INSTANCE == null) INSTANCE = new MyApplication();
        return INSTANCE;
    }

    public static OwnerProperties getOwnerProperties(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String settingDeviceName = sharedPref.getString(context.getString(R.string.setting_device_name), Build.MODEL);
        OwnerProperties ownerProperties = new OwnerProperties(ServerService.getPORT(), settingDeviceName, "A");
        return ownerProperties;
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
            sslContext = SSLContext.getInstance(Constants.TLS_1_2);
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
