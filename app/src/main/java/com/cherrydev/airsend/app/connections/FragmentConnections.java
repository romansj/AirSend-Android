package com.cherrydev.airsend.app.connections;

import static com.cherrydev.airsend.app.MyApplication.databaseManager;
import static com.cherrydev.airsend.app.settings.PreferenceKey.DEVICE_NAME;
import static com.cherrydev.airsend.app.settings.PreferenceKey.LAST_USED_PSK;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.AppViewModel;
import com.cherrydev.airsend.app.connections.qr.BarcodeUtils;
import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsend.app.service.ServerService;
import com.cherrydev.airsend.app.settings.PreferenceUtils;
import com.cherrydev.airsend.app.utils.DialogRecyclerViewAction;
import com.cherrydev.airsend.app.utils.NetworkUtils;
import com.cherrydev.airsend.databinding.FragmentConnectionsBinding;
import com.cherrydev.clipboard.ClipboardUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.romansj.core.client.ClientManager;
import io.github.romansj.core.server.ServerEvent;
import io.github.romansj.core.utils.CoreNetworkUtils;
import io.github.romansj.core.utils.SSLUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("SetTextI18n")
public class FragmentConnections extends Fragment {

    private AppViewModel viewModel;
    private FragmentConnectionsBinding binding;
    private RecylerViewAdapterDevice adapter;
    private BottomSheetBehavior bottomSheetBehavior;


    public static FragmentConnections newInstance() {
        return new FragmentConnections();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConnectionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);


        initBottomSheetDeviceInfo();


        RecylerViewAdapterDevice.OnClickListener clickListener = this::handleDeviceClick;
        adapter = new RecylerViewAdapterDevice(clickListener);
        adapter.setHasStableIds(true);
        viewModel.getDevices().observe(getViewLifecycleOwner(), devices -> {
            List<DeviceWrapper> deviceWrapperList = new ArrayList<>();
            long id = 0;
            for (var device : devices) {
                deviceWrapperList.add(new DeviceWrapper(id, device));
                id++;
            }

            adapter.updateData(deviceWrapperList);

            binding.tvEmptyRv.setVisibility(devices.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerView.setVisibility(!devices.isEmpty() ? View.VISIBLE : View.GONE);
        });


        var recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


        binding.btnStop.setOnClickListener(v -> ServerService.stopService());
        binding.btnStart.setOnClickListener(v -> initServer());
        viewModel.getServerEventMutableLiveData().observe(getViewLifecycleOwner(), event -> {
            var text = getServerStatusText(event);
            binding.tvServerStatus.setText(text);
        });


        binding.btnDisconnectAll.setOnClickListener(v -> ClientManager.getInstance().disconnectAll());
        binding.btnConnectAll.setOnClickListener(v -> {
            if (isNotConnected()) return;

            databaseManager.getDb().getAllDevices().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(devices -> {
                var pairs = devices.stream().map(p -> Pair.of(p.getIP(), p.getPort())).collect(Collectors.toList());
                ClientManager.getInstance().connectToList(pairs);
            });
        });


        binding.fabConnect.setOnClickListener(v -> {
            if (isNotConnected()) return;

            DialogMakeConnection.DialogMakeConnectionListener listener = (IP, port, psk) -> {
                connectToDevice(IP, port, psk);
                PreferenceUtils.updatePreference(LAST_USED_PSK, psk);
            };
            DialogMakeConnection.newInstance(listener).show(getParentFragmentManager(), null);

        });
    }

    @NonNull
    private String getServerStatusText(ServerEvent event) {
        var text = "";
        switch (event.getOperation()) {
            case START:
                text = "Starting";
                break;
            case STOP:
                text = "Stopped";
                break;
            case LISTEN:
                text = "Running";
                break;
        }
        return text;
    }

    /**
     * Equalise PSKs and connect to device. Equalise - remote device PSK = our PSK
     *
     * @param IP   device IP address
     * @param port device port
     * @param psk  device psk
     */
    private void connectToDevice(String IP, int port, String psk) {
        ClientManager clientManager = ClientManager.getInstance();
        clientManager.setSslSocketFactory(SSLUtils.getSSLSocketFactory(getDeviceName(), psk));
        clientManager.connect(IP, port);

        Toast.makeText(requireActivity(), requireContext().getString(R.string.message_sent), Toast.LENGTH_LONG).show();
    }

    private String getDeviceName() {
        return PreferenceUtils.getString(DEVICE_NAME);
    }


    private void handleDeviceClick(Device connectionItem, RecylerViewAdapterDevice.ClickItem clickItem) {
        String itemIP = connectionItem.getIP();
        DialogRecyclerViewAction.DialogButtonListener<DeviceActionWrapper> listener = getDeviceActionWrapperDialogButtonListener(connectionItem, itemIP);


        switch (clickItem) {
            case CLICK:
                ClipboardUtils.copyToClipboard(requireContext(), itemIP);
                Toast.makeText(requireContext(), requireContext().getString(R.string.copied_IP_to_clipboard), Toast.LENGTH_LONG).show();
                break;

            case LONG_CLICK: {
                DialogRecyclerViewAction<DeviceActionWrapper> dialog = DialogRecyclerViewAction.newInstance(getString(R.string.device_actions), getString(R.string.what_would_you_like_to_do_with_the_device), List.of(
                                new DeviceActionWrapper(DeviceAction.CONNECT, requireContext().getString(R.string.connect)),
                                new DeviceActionWrapper(DeviceAction.DISCONNECT, requireContext().getString(R.string.disconnect)),
                                new DeviceActionWrapper(DeviceAction.DELETE, requireContext().getString(R.string.delete))),
                        "", getString(R.string.cancel), listener);

                dialog.show(getParentFragmentManager(), null);

                break;
            }
        }
    }

    @NonNull
    private DialogRecyclerViewAction.DialogButtonListener<DeviceActionWrapper> getDeviceActionWrapperDialogButtonListener(Device connectionItem, String itemIP) {
        int itemPort = connectionItem.getPort();


        DialogRecyclerViewAction.DialogButtonListener<DeviceActionWrapper> listener = item -> {
            switch (item.getDeviceAction()) {
                case CONNECT:
                    if (isNotConnected()) break;
                    ClientManager.getInstance().connect(itemIP, itemPort);
                    break;
                case DISCONNECT:
                    ClientManager.getInstance().disconnect(itemIP, itemPort);
                    break;
                case DELETE:
                    ClientManager.getInstance().deleteClient(itemIP, itemPort);
                    break;
            }
        };
        return listener;
    }

    private void initBottomSheetDeviceInfo() {
        View bottomSheet = binding.bottomSheet;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    showDeviceData();
                    binding.btnDeviceInfo.setIcon(getDrawable(R.drawable.ic_keyboard_arrow_down));
                } else {
                    binding.btnDeviceInfo.setIcon(getDrawable(R.drawable.arrow_up));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        LinearLayout buttonReceive = binding.buttonReceive;
        buttonReceive.setOnClickListener(v -> showHideReceiveLayout());
    }


    private Drawable getDrawable(int id) {
        return requireContext().getDrawable(id);
    }


    @SuppressLint("CheckResult")
    private void showDeviceData() {
        ImageView myImage = binding.imageViewQr;

        SSLUtils.printSupportedProtocols();
        var ip = CoreNetworkUtils.getIPAddress(false);
        TextView ipTV = binding.ipTv;

        String settingPsk = PreferenceUtils.getString(LAST_USED_PSK);

        if (ServerService.getPORT() == 0 || ip.equals("null")) {
            ipTV.setText(getString(R.string.server_is_not_started));
            BarcodeUtils.getBitmap(getString(R.string.server_is_not_started)).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> myImage.setImageBitmap(bitmap));

        } else {
            ipTV.setText(ip + " | PORT: " + ServerService.getPORT());
            BarcodeUtils.getBitmap(String.format("%s,%s,%s", ip, ServerService.getPORT(), settingPsk)).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> myImage.setImageBitmap(bitmap));
        }


        binding.pskTv.setText(settingPsk);
    }


    private void initServer() {
        ServerService.startService();
    }


    private void showHideReceiveLayout() {
        int state = bottomSheetBehavior.getState();
        boolean curVisB = (state == BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(curVisB ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_EXPANDED);
    }


    private boolean isNotConnected() {
        if (!NetworkUtils.isConnectedToNetwork(requireContext())) {
            Toast.makeText(requireContext(), getString(R.string.not_connected_to_network), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}