package com.cherrydev.airsend.app.settings;

import static android.content.Context.POWER_SERVICE;
import static android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsend.app.utils.mymodels.EditTextDebounce;
import com.cherrydev.airsend.databinding.FragmentSettingsBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;


public class FragmentSettings extends Fragment {

    private FragmentSettingsBinding binding;

    public static FragmentSettings newInstance() {
        FragmentSettings fragment = new FragmentSettings();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                binding.switchShowNotificationsMessage
        );


        SharedPreferences sharedPref = MyApplication.getInstance().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        switchList.forEach(switchMaterial -> {
            //problem on restore saved instance (if user changed, default is overwritten if no value has yet been set to sharepref)
            boolean isChecked = sharedPref.getBoolean((String) switchMaterial.getTag(), switchMaterial.isChecked());
            switchMaterial.setChecked(isChecked);
        });


        CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> {
            String tag = (String) buttonView.getTag();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(tag, isChecked);
            editor.apply();
        };

        switchList.forEach(switchMaterial -> switchMaterial.setOnCheckedChangeListener(checkedChangeListener));


        boolean isChecked = sharedPref.getBoolean((String) binding.btnGroupSendClipboard.getTag(), binding.btnGroupSendClipboard.getCheckedButtonId() == binding.btnSendClipboardAll.getId());
        binding.btnGroupSendClipboard.check(isChecked ? binding.btnSendClipboardAll.getId() : binding.btnSendClipboardAsk.getId());

        binding.btnGroupSendClipboard.addOnButtonCheckedListener((group, checkedId, isChecked1) -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean((String) binding.btnGroupSendClipboard.getTag(), checkedId == binding.btnSendClipboardAll.getId());
            editor.apply();
        });


        String settingDeviceName = sharedPref.getString(getString(R.string.setting_device_name), Build.MODEL);
        EditText editText = binding.textInputDeviceName.getEditText();
        editText.setText(settingDeviceName.trim());

        EditTextDebounce.create(editText).watch(result -> {
            if (result.contains(",")) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.you_cannot_enter_commas_device_name), Toast.LENGTH_LONG).show();
                editText.setText(settingDeviceName);
                return;
            }


            sharedPref.edit()
                    .putString(getString(R.string.setting_device_name), result.trim().isEmpty() ? Build.MODEL : result.trim())
                    .apply();
        }, 200);


        updateBatteryOptButton();

        binding.buttonDisableBattOpt.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        });


        binding.btnLicenses.setOnClickListener(v -> DialogLicenses.newInstance().show(getParentFragmentManager(), null));

        binding.tvMadeBy.setOnClickListener(v -> {
            countClickedAbout++;
            if (countClickedAbout == 3) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean((String) v.getTag(), true);
                editor.apply();

                Toast.makeText(requireContext(), "You have enabled developer settings. Please restart the app.", Toast.LENGTH_LONG).show();
            }
        });

        binding.tvMadeBy.setOnLongClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean((String) v.getTag(), false);
            editor.apply();

            Toast.makeText(requireContext(), "You have enabled developer settings. Please restart the app.", Toast.LENGTH_LONG).show();
            return true;
        });
    }

    int countClickedAbout = 0;


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