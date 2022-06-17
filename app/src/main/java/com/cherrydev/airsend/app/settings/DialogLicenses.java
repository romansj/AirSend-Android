package com.cherrydev.airsend.app.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.databinding.DialogInfoBinding;

import java.util.List;

public class DialogLicenses extends DialogFragment {

    public static DialogLicenses newInstance() {

        Bundle args = new Bundle();

        DialogLicenses fragment = new DialogLicenses();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        var rxAndroid = new LicenseItem("RxAndroid", "https://github.com/ReactiveX/RxAndroid", "https://github.com/ReactiveX/RxAndroid/blob/3.x/LICENSE");
        var roomDatabase = new LicenseItem("Room Database", "https://developer.android.com/training/data-storage/room", "https://github.com/androidx-releases/Room/blob/master/LICENSE");
        var conscrypt = new LicenseItem("Conscrypt - A Java Security Provider", "https://github.com/google/conscrypt", "https://github.com/google/conscrypt/blob/master/LICENSE");
        var qrGen = new LicenseItem("QRGen", "https://github.com/kenglxn/QRGen", "http://www.apache.org/licenses/LICENSE-2.0.html");
        var timber = new LicenseItem("Timber", "https://github.com/JakeWharton/timber", "https://github.com/JakeWharton/timber/blob/trunk/LICENSE.txt");
        var benchit = new LicenseItem("Benchit", "https://github.com/T-Spoon/Benchit", "https://github.com/T-Spoon/Benchit/blob/master/LICENSE");
        var icons = new LicenseItem("Icons", "https://materialdesignicons.com/", "https://github.com/Templarian/MaterialDesign/blob/master/LICENSE");
        var googleGuava = new LicenseItem("Google Guava", "https://github.com/google/guava", "https://github.com/google/guava/blob/master/COPYING");

        var licenseItems = List.of(rxAndroid, roomDatabase, conscrypt, qrGen, timber, benchit, icons, googleGuava);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MaterialAlertDialog_MaterialComponents);
        DialogInfoBinding binding = DialogInfoBinding.inflate(LayoutInflater.from(requireContext()));

        binding.tvTitle.setText(getString(R.string.licenses));

        LicenseAdapter adapter = new LicenseAdapter(licenseItems);
        adapter.setHasStableIds(false);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        builder.setView(binding.getRoot());
        builder.setPositiveButton(getString(R.string.close), null);
        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }
}
