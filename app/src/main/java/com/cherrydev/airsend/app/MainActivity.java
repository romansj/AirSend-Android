package com.cherrydev.airsend.app;

import static com.cherrydev.airsend.app.MyApplication.databaseManager;
import static com.cherrydev.airsend.app.settings.PreferenceKey.LAST_USED_PSK;
import static com.cherrydev.airsend.app.settings.PreferenceKey.setting_debug_options;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.database.ClientHandlerImpl;
import com.cherrydev.airsend.app.messages.SentMessageHandlerImpl;
import com.cherrydev.airsend.app.service.ServerService;
import com.cherrydev.airsend.app.settings.PreferenceUtils;
import com.cherrydev.airsend.app.utils.AppUtils;
import com.cherrydev.airsend.app.utils.IntentAction;
import com.cherrydev.airsend.app.utils.NavUtils;
import com.cherrydev.airsend.databinding.ActivityMainBinding;
import com.cherrydev.common.MimeTypes;

import org.apache.commons.lang3.tuple.Pair;

import java.util.stream.Collectors;

import io.github.romansj.core.client.ClientManager;
import io.github.romansj.core.ssl.SSLUtils;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_NAVIGATION_ITEM_ID = "KEY_NAVIGATION_ITEM_ID";

    private ActivityMainBinding binding;
    private AppViewModel viewModel;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NavUtils navUtils;


    @SuppressLint({"NonConstantResourceId", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        viewModel = new ViewModelProvider(MainActivity.this).get(AppViewModel.class);
        navUtils = new NavUtils(getSupportFragmentManager());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_messages:
                    navUtils.navigate(NavUtils.Tag.MESSAGES);
                    return true;

                case R.id.navigation_connections:
                    navUtils.navigate(NavUtils.Tag.CONNECTIONS);
                    return true;

                case R.id.navigation_settings:
                    navUtils.navigate(NavUtils.Tag.SETTINGS);
                    return true;
            }
            return false;

        });


        navUtils.setNavigateCallback(to -> viewModel.setNavigationTag(to));


        // process death
        if (savedInstanceState != null) {
            var navSelectedID = savedInstanceState.getInt(KEY_NAVIGATION_ITEM_ID);
            binding.bottomNav.setSelectedItemId(navSelectedID);

        } else { // configuration change
            var navigationTag = viewModel.getNavigationTag();
            navUtils.navigate(navigationTag);
        }


        binding.btnRestart.setOnClickListener(v -> AppUtils.restartApp());
        binding.btnKill.setOnClickListener(v -> AppUtils.killApp());
        binding.btnNukeDb.setOnClickListener(v -> {
            databaseManager.nukeAll().runInBackground().run();
            AppUtils.restartApp();
        });


        ServerService.startService();

        // called here to set the ClientHandler before any other invocation
        String settingPsk = PreferenceUtils.getString(LAST_USED_PSK);
        ClientManager clientManager = ClientManager.getInstance();
        clientManager.setSslSocketFactory(SSLUtils.getSSLSocketFactory("android", settingPsk));

        clientManager.setMessageHandler(new SentMessageHandlerImpl());
        clientManager.setClientHandler(new ClientHandlerImpl());

        Intent intent = getIntent();
        handleIntent(intent, savedInstanceState);


        initNetworkListener();


        boolean isChecked = PreferenceUtils.getBoolean(setting_debug_options);
        binding.linearDebug.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }


    private void initNetworkListener() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            // The default network is now: 182
            @Override
            public void onAvailable(Network network) {
                Timber.d("ClientSSL " + "The default network is now: " + network);

                var ownerProperties = MyApplication.getOwnerProperties();
                ClientManager.getInstance().setOwnerProperties(ownerProperties);

                databaseManager.getDb().getAllDevices().subscribeOn(Schedulers.io()).subscribe(devices -> {
                    var pairs = devices.stream().map(p -> Pair.of(p.getIP(), p.getPort())).collect(Collectors.toList());
                    ClientManager.getInstance().connectToList(pairs);
                });
            }

            // The application no longer has a default network. The last default network was 182
            @Override
            public void onLost(Network network) {
                Timber.d("ClientSSL " + "The application no longer has a default network. The last default network was " + network);

                ClientManager.getInstance().disconnectAll();
            }

            // The default network changed capabilities: [ Transports: WIFI Capabilities: NOT_METERED&INTERNET&NOT_RESTRICTED&TRUSTED&NOT_VPN&NOT_ROAMING&FOREGROUND&NOT_CONGESTED&NOT_SUSPENDED LinkUpBandwidth>=465054Kbps LinkDnBandwidth>=367148Kbps SignalStrength: -67 AdministratorUids: [] RequestorUid: -1 RequestorPackageName: null]
            // The default network changed capabilities: [ Transports: WIFI Capabilities: NOT_METERED&INTERNET&NOT_RESTRICTED&TRUSTED&NOT_VPN&VALIDATED&NOT_ROAMING&FOREGROUND&NOT_CONGESTED&NOT_SUSPENDED LinkUpBandwidth>=465054Kbps LinkDnBandwidth>=367148Kbps SignalStrength: -67 AdministratorUids: [] RequestorUid: -1 RequestorPackageName: null]
            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                // Log.e("NavUtils.Tag", "The default network changed capabilities: " + networkCapabilities);
            }

            // The default network changed link properties: {InterfaceName: wlan0 LinkAddresses: [ fe80::2c60:a2ff:fe60:803/64,192.168.1.102/24 ] DnsAddresses: [ /192.168.1.254 ] Domains: null MTU: 0 ServerAddress: /192.168.1.254 TcpBufferSizes: 524288,1048576,4194304,524288,1048576,4194304 Routes: [ fe80::/64 -> :: wlan0 mtu 0,192.168.1.0/24 -> 0.0.0.0 wlan0 mtu 0,0.0.0.0/0 -> 192.168.1.254 wlan0 mtu 0 ]}
            @Override
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                // Log.e("NavUtils.Tag", "The default network changed link properties: " + linkProperties);
            }
        };


        ConnectivityManager connectivityManager = MyApplication.getInstance().getSystemService(ConnectivityManager.class);
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    private void handleSendText(Intent intent, Bundle savedState) {
        navUtils.navigate(NavUtils.Tag.MESSAGES);


        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        //forward to connected devices or ask to connect to a new/saved device
        Toast.makeText(this, "Received text: " + sharedText, Toast.LENGTH_LONG).show();
        viewModel.setTextToSend(sharedText);
    }

    private void handleIntent(Intent intent, Bundle savedState) {
        String action = intent.getAction();
        String type = intent.getType();

        Timber.d("handleIntent::action %s", action);

        IntentAction intentAction = null;
        try {
            intentAction = IntentAction.valueOf(action);
        } catch (IllegalArgumentException e) {
            // e.g., MAIN (launcher intent)
            // e.printStackTrace();
            return;
        }
        switch (intentAction) {
            case ACTION_SEND: // android action to share to app
                Timber.d("send action main act");
                if (MimeTypes.Text.PLAIN.equals(type)) handleSendText(intent, savedState);
                break;

            case ACTION_SHARE_CLIPBOARD:
                navUtils.navigate(NavUtils.Tag.MESSAGES);
                viewModel.setNeedToCopyClipboard(true);
                //get current clipboard data
                //show device list for user to pick which to send to
                break;

            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //https://stackoverflow.com/a/27113237/4673960 single task
        handleIntent(intent, null);
    }

    @Override
    protected void onDestroy() {
        if (networkCallback != null) {
            ConnectivityManager connectivityManager = MyApplication.getInstance().getSystemService(ConnectivityManager.class);
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        ClientManager.getInstance().disconnectAll();


        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_NAVIGATION_ITEM_ID, binding.bottomNav.getSelectedItemId());

        super.onSaveInstanceState(outState);
    }
}