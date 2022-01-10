package com.cherrydev.airsend.app.utils;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.app.utils.mymodels.SimpleListAdapter;
import com.cherrydev.airsend.databinding.DialogInfoListBinding;

import java.util.List;


public class DialogRecyclerViewAction<T extends DialogRecyclerViewAction.DialogActionItemInterface> extends DialogFragment {

    public static final String ARG_TITLE = "key_title";
    public static final String ARG_MESSAGE = "key_message";
    public static final String ARG_POSITIVE_BUTTON_TEXT = "key_positive";
    public static final String ARG_NEGATIVE_BUTTON_TEXT = "key_negative";
    public static final String ARG_LIST = "ARG_NEGATIVE_BUTTON_TEXT";

    private DialogButtonListener<T> listener;
    public static final String ARG_LINK = "ARG_LINK";
    private List<T> list;

    public void setListener(DialogButtonListener<T> listener) {
        this.listener = listener;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public static <T extends DialogActionItemInterface> DialogRecyclerViewAction<T> newInstance(String title, String message, List<T> list, String positiveText, String negativeText, DialogButtonListener<T> listener) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON_TEXT, positiveText);
        args.putString(ARG_NEGATIVE_BUTTON_TEXT, negativeText);


        DialogRecyclerViewAction<T> fragment = new DialogRecyclerViewAction<>();
        fragment.setArguments(args);
        fragment.setListener(listener);
        fragment.setList(list);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE, "");
        String message = args.getString(ARG_MESSAGE, "");
        String positive = args.getString(ARG_POSITIVE_BUTTON_TEXT, "");
        String negative = args.getString(ARG_NEGATIVE_BUTTON_TEXT, "");
        //ArrayList<String> list = args.getStringArrayList(ARG_LIST);
        String link = args.getString(ARG_LINK, "%link%");


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());


        if (!positive.isEmpty()) {
            builder.setPositiveButton(positive, (dialogInterface, i) -> {
                if (listener != null) listener.onPositiveButtonClicked();
            });
        }
        if (!negative.isEmpty()) {
            builder.setNegativeButton(negative, (dialog, which) -> {
                if (listener != null) listener.onNegativeButtonClicked();
            });
        }


        DialogInfoListBinding binding = DialogInfoListBinding.inflate(LayoutInflater.from(requireContext()));
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        SimpleListAdapter<T> adapter = new SimpleListAdapter<T>(list);
        adapter.setOnClickListener(new SimpleListAdapter.OnClickListener<T>() {

            @Override
            public void onClick(T item) {
                listener.onRvItemClicked(item);
                dismiss();
            }

            @Override
            public void onLongClick(T item) {

            }

        });
        recyclerView.setAdapter(adapter);
        if (!title.isEmpty()) binding.tvTitle.setText(title);
        if (!message.isEmpty()) binding.tvDescription.setText(message);

        builder.setView(binding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }


    private void openBrowser(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public interface DialogActionItemInterface {
        String getText();
    }


    public interface DialogButtonListener<T> {
        void onRvItemClicked(T item);

        default void onPositiveButtonClicked() {

        }

        default void onNegativeButtonClicked() {

        }
    }
}
