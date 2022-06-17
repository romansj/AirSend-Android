package com.cherrydev.airsend.app.messages.recipient;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.database.models.Device;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DialogChooseRecipients extends DialogFragment {

    private RecipientChoiceAdapter adapter;
    private List<Device> devicesList = new ArrayList<>();
    private List<Device> selectedItems = new ArrayList<>();
    private DialogListener listener;
    private EditText editText;
    private boolean wasModified = false;


    public static DialogChooseRecipients newInstance(List<Device> devicesList, List<Device> selectedDevices, DialogListener listener) {
        DialogChooseRecipients fragment = new DialogChooseRecipients();
        fragment.setDevicesList(devicesList);
        fragment.setSelectedItems(selectedDevices);
        fragment.setListener(listener);
        return fragment;
    }


    public void setDevicesList(List<Device> devicesList) {
        this.devicesList.addAll(devicesList);
    }

    public void setSelectedItems(List<Device> selectedItems) {
        this.selectedItems.addAll(selectedItems);
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_rv_search, null);
        AlertDialog alertDialog = getAlertDialog(dialogView);

        initRecyclerView(dialogView);
        initEditText(dialogView);

        return alertDialog;
    }


    @NotNull
    private AlertDialog getAlertDialog(View dialogView) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder
                .setView(dialogView)
                .setTitle(getString(R.string.select_recipients))
                .setCancelable(true)
                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> listener.onDialogClosed(adapter.getCheckedItems()))
                .setNegativeButton(getString(R.string.close), (dialog, which) -> listener.onDialogClosed(adapter.getCheckedItems()));


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return alertDialog;
    }

    private void initRecyclerView(View dialogView) {
        RecyclerView recyclerView = dialogView.findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        adapter = new RecipientChoiceAdapter(devicesList, selectedItems);
        adapter.setHasStableIds(false);
        recyclerView.setAdapter(adapter);

    }

    private void initEditText(View dialogView) {
        editText = dialogView.findViewById(R.id.editText);

        editText.post(() -> editText.addTextChangedListener(new TextWatcher() {
            //https://stackoverflow.com/a/29449717/4673960

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }));
    }

    private void filter(String toString) {
        toString = toString.trim().toLowerCase();
        if (toString.isEmpty()) {
            adapter.updateData(devicesList);
            return;
        }

        List<Device> filtered = new ArrayList<>();
        for (Device device : devicesList) {
            if (device.getName().toLowerCase().contains(toString) || device.getIP().toLowerCase().contains(toString)) {
                filtered.add(device);
            }
        }
        adapter.updateData(filtered);


    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        listener.onDialogClosed(adapter.getCheckedItems());
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        listener.onDialogClosed(adapter.getCheckedItems());
        super.onCancel(dialog);
    }

    public interface DialogListener {
        void onDialogClosed(List<Device> checkedItems);
    }


}