package com.cherrydev.airsend.app.utils;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;

public class ClipboardUtils {
    /**
     * Copies given String to the clipboard
     *
     * @param text : text to copy
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Returns empty string if nothing is stored on the clipboard or if it's not text.
     *
     * @return Text stored in the clipboard.
     */
    @NonNull
    public static String getClipboardText(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String textToReturn = "";

        //nothing in the clipboard
        if (!clipboard.hasPrimaryClip()) return textToReturn;

        ClipData primaryClip = clipboard.getPrimaryClip();
        if (primaryClip == null) return textToReturn; //null check necessitated by Android
        if (!primaryClip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))
            return textToReturn; //not text

        //good text in clipboard to paste
        textToReturn = primaryClip.getItemAt(0).getText().toString().trim();
        return textToReturn;
    }
}
