package com.cherrydev.airsend.app.settings;

import static android.content.Context.POWER_SERVICE;
import static android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS;
import static com.cherrydev.airsend.app.settings.PreferenceKey.DEVICE_NAME;
import static com.cherrydev.airsend.app.settings.PreferenceKey.LAST_USED_PSK;
import static com.cherrydev.airsend.app.settings.PreferenceKey.SEND_CLIPBOARD;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.settings.license.DialogLicenses;
import com.cherrydev.airsend.app.utils.PskUtils;
import com.cherrydev.airsend.app.utils.mymodels.EditTextDebounce;
import com.cherrydev.airsend.databinding.FragmentSettingsBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;


public class FragmentSettings extends Fragment {

    private int countClickedAbout = 0;
    private FragmentSettingsBinding binding;

    public static FragmentSettings newInstance() {
        FragmentSettings fragment = new FragmentSettings();
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<SwitchMaterial> switchList = List.of(
                binding.switchConnectToKnown,
                binding.switchStartOnBoot,
                binding.switchShowNotificationsConnection,
                binding.switchShowNotificationsMessage,
                binding.switchOpenLinksOnClick
        );


        switchList.forEach(switchMaterial -> {
            // problem on restore saved instance (if user changed, default is overwritten if no value has yet been set to sharepref)
            boolean isChecked = PreferenceUtils.getBoolean(PreferenceKey.valueOf(switchMaterial.getTag().toString()));
            switchMaterial.setChecked(isChecked);
        });


        CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> {
            String tag = (String) buttonView.getTag();
            PreferenceUtils.updatePreference(PreferenceKey.valueOf(tag), isChecked);
        };

        switchList.forEach(switchMaterial -> switchMaterial.setOnCheckedChangeListener(checkedChangeListener));


        boolean isChecked = PreferenceUtils.getBoolean(SEND_CLIPBOARD);
        binding.btnGroupSendClipboard.check(isChecked ? binding.btnSendClipboardAll.getId() : binding.btnSendClipboardAsk.getId());

        binding.btnGroupSendClipboard.addOnButtonCheckedListener((group, checkedId, isChecked1) -> {
            boolean sendAllChecked = checkedId == binding.btnSendClipboardAll.getId();
            PreferenceUtils.updatePreference(SEND_CLIPBOARD, sendAllChecked);
        });


        String settingDeviceName = PreferenceUtils.getString(PreferenceKey.DEVICE_NAME);
        EditText editText = binding.textInputDeviceName.getEditText();
        editText.setText(settingDeviceName.trim());

        EditTextDebounce.create(editText).watch(result -> {
            if (result.contains(",")) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.you_cannot_enter_commas_device_name), Toast.LENGTH_LONG).show();
                editText.setText(settingDeviceName);
                return;
            }

            String deviceName = result.trim().isEmpty() ? Build.MODEL : result.trim();
            PreferenceUtils.updatePreference(DEVICE_NAME, deviceName);

        }, 200);


        updatePSKSettings();
        binding.btnRefreshPsk.setOnClickListener(v -> {
            String randomPsk = PskUtils.getRandomPsk();
            PreferenceUtils.updatePreference(PreferenceKey.LAST_USED_PSK, randomPsk);
            updatePSKSettings();
        });


        updateBatteryOptButton();

        binding.buttonDisableBattOpt.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        });


        binding.btnLicenses.setOnClickListener(v -> DialogLicenses.newInstance().show(getParentFragmentManager(), null));


        devSettings();
    }

    private void devSettings() {
        binding.imageViewAbout.setOnClickListener(v -> {
            countClickedAbout++;
            if (countClickedAbout == 3) {
                String preferenceKey = (String) v.getTag();
                PreferenceUtils.updatePreference(PreferenceKey.valueOf(preferenceKey), true);
                Toast.makeText(requireContext(), "You have enabled developer settings. Please restart the app.", Toast.LENGTH_LONG).show();
            }
        });

        binding.imageViewAbout.setOnLongClickListener(v -> {
            String preferenceKey = (String) v.getTag();
            PreferenceUtils.updatePreference(PreferenceKey.valueOf(preferenceKey), false);
            Toast.makeText(requireContext(), "You have disabled developer settings. Please restart the app.", Toast.LENGTH_LONG).show();
            return true;
        });
    }

    private void updatePSKSettings() {
        String settingPsk = PreferenceUtils.getString(LAST_USED_PSK);
        EditText editTextPsk = binding.textInputPsk.getEditText();
        editTextPsk.setText(settingPsk);

        EditTextDebounce.create(editTextPsk).watch(result -> {
            PreferenceUtils.updatePreference(LAST_USED_PSK, result);
        }, 200);
    }


    private void updateBatteryOptButton() {
        String packageName = requireActivity().getPackageName();
        PowerManager pm = (PowerManager) requireActivity().getSystemService(POWER_SERVICE);

        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            binding.buttonDisableBattOpt.setEnabled(false);
            binding.buttonDisableBattOpt.setText(R.string.already_done);

        } else {
            binding.buttonDisableBattOpt.setEnabled(true);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        updateBatteryOptButton();
    }
}