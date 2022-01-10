package com.cherrydev.airsend.app.utils.mymodels;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public final class EditTextDebounce {

    //https://gist.github.com/demixdn/1267fa215824e2d5111e5321a7184721

    private final WeakReference<EditText> editTextWeakReference;
    private final Handler debounceHandler;
    private DebounceCallback debounceCallback;
    private Runnable debounceWorker;
    private int delayMillis;
    private final TextWatcher textWatcher;

    public static EditTextDebounce create(@NonNull EditText editText) {
        return new EditTextDebounce(editText);
    }

    public static EditTextDebounce create(@NonNull EditText editText, int delayMillis) {
        EditTextDebounce editTextDebounce = new EditTextDebounce(editText);
        editTextDebounce.setDelayMillis(delayMillis);
        return editTextDebounce;
    }

    private EditTextDebounce(@NonNull EditText editText) {
        this.debounceHandler = new Handler(Looper.getMainLooper());
        this.debounceWorker = new DebounceRunnable("", null);
        this.delayMillis = 300;
        this.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //unused
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //unused
            }

            @Override
            public void afterTextChanged(Editable s) {
                debounceHandler.removeCallbacks(debounceWorker);
                debounceWorker = new DebounceRunnable(s.toString(), debounceCallback);
                debounceHandler.postDelayed(debounceWorker, delayMillis);
            }
        };
        this.editTextWeakReference = new WeakReference<>(editText);
        EditText editTextInternal = this.editTextWeakReference.get();
        if (editTextInternal != null) {
            editTextInternal.addTextChangedListener(textWatcher);
        }
    }

    public void watch(@Nullable DebounceCallback debounceCallback) {
        this.debounceCallback = debounceCallback;
    }

    public void watch(@Nullable DebounceCallback debounceCallback, int delayMillis) {
        this.debounceCallback = debounceCallback;
        this.delayMillis = delayMillis;
    }

    public void unwatch() {
        if (editTextWeakReference != null) {
            EditText editText = editTextWeakReference.get();
            if (editText != null) {
                editText.removeTextChangedListener(textWatcher);
                editTextWeakReference.clear();
                debounceHandler.removeCallbacks(debounceWorker);
            }
        }
    }

    private void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    private static class DebounceRunnable implements Runnable {

        private final String result;
        private final DebounceCallback debounceCallback;

        DebounceRunnable(String result, DebounceCallback debounceCallback) {
            this.result = result;
            this.debounceCallback = debounceCallback;
        }

        @Override
        public void run() {
            if (debounceCallback != null) {
                debounceCallback.onFinished(result);
            }
        }
    }

    public interface DebounceCallback {
        void onFinished(@NonNull String result);
    }

}