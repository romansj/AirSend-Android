package com.cherrydev.airsend.app.connections;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.connections.qr.ScanBarcodeActivity;
import com.cherrydev.airsend.databinding.DialogMakeConnectionBinding;

import timber.log.Timber;

public class DialogMakeConnection extends DialogFragment {

    private final int REQUEST_CODE_GET_QR = 1001;
    private DialogMakeConnectionListener listener;


    public static DialogMakeConnection newInstance(DialogMakeConnectionListener listener) {
        Bundle args = new Bundle();
        DialogMakeConnection fragment = new DialogMakeConnection();
        fragment.setListener(listener);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogMakeConnectionBinding binding = DialogMakeConnectionBinding.inflate(getLayoutInflater());

        binding.btnScanQr.setOnClickListener(v -> startActivityForResult(new Intent(requireContext(), ScanBarcodeActivity.class), REQUEST_CODE_GET_QR));
        binding.btnConnect.setOnClickListener(v -> {
            var IPText = binding.inputLayoutIp.getEditText().getText().toString();
            var portText = binding.inputLayoutPort.getEditText().getText().toString();
            var pskText = binding.inputLayoutPsk.getEditText().getText().toString();

            validateInputAndClose(IPText, portText, pskText);
        });
        binding.btnClose.setOnClickListener(v -> dismiss());


        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    private void validateInputAndClose(String IPText, String portText, String pskText) {

        if (!InputValidator.validateConnectionParams(IPText, portText, pskText)) {
            Toast.makeText(requireContext(), getString(R.string.please_fill_in_all_required_fields), Toast.LENGTH_LONG).show();
            return;
        }

        String IP = IPText.trim();
        int port = Integer.parseInt(portText);
        String psk = pskText.trim();


        if (listener != null) listener.onDialogClosed(IP, port, psk);
        dismiss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE_GET_QR) return;
        if (resultCode != Activity.RESULT_OK) return;


        String result = data.getStringExtra("RESULT");
        Timber.d("result: " + result);

        String[] split = result.split(",");

        if (split.length < 3) {
            Toast.makeText(requireContext(), getString(R.string.incorrect_qr_format), Toast.LENGTH_LONG).show();
            return;
        }

        String ip = split[0].trim();
        String port = split[1].trim();
        String psk = split[2].trim();


        validateInputAndClose(ip, port, psk);
    }

    private void setListener(DialogMakeConnectionListener listener) {
        this.listener = listener;
    }

    public interface DialogMakeConnectionListener {
        void onDialogClosed(String IP, int port, String psk);
    }
}
