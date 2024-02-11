package com.cherrydev.airsend.app.settings;

import static com.cherrydev.airsend.app.settings.PreferenceKey.DEVICE_NAME;
import static com.cherrydev.airsend.app.settings.PreferenceKey.LAST_USED_PSK;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsend.app.utils.PskUtils;

import java.util.Map;

public class PreferenceUtils {

    static Map<PreferenceKey, Boolean> mapBooleanDefaults = Map.of(
    );

    static Map<PreferenceKey, String> mapStringDefaults = Map.of(
            LAST_USED_PSK, PskUtils.getRandomPsk(),
            DEVICE_NAME, Build.MODEL
    );

    static Map<PreferenceKey, Integer> mapIntDefaults = Map.of(
            PreferenceKey.LAST_USED_PORT, 0
    );


    public static final String PREFERENCES_NAME = "com.cherrydev.airsend.prefs";

    private PreferenceUtils() {
    }

//        getPreferences().getLong();
//        getPreferences().getInt();
//        getPreferences().getFloat();
//        getPreferences().getBoolean();
//        getPreferences().getString();

    public static void updatePreference(PreferenceKey preference, String value) {
        getEditor().putString(preference.name(), value).commit();
    }

    public static void updatePreference(PreferenceKey preference, boolean value) {
        getEditor().putBoolean(preference.name(), value).commit();
    }

    public static void updatePreference(PreferenceKey preferenceKey, int value) {
        getEditor().putInt(preferenceKey.name(), value);
    }


    private static SharedPreferences.Editor getEditor() {
        return getPreferences().edit();
    }

    private static SharedPreferences getPreferences() {
        return MyApplication.getInstance().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }


    public static String getString(PreferenceKey key) {
        return getPreferences().getString(key.name(), getDefaultStringValue(key));
    }

    public static boolean getBoolean(String key) {
        return getPreferences().getBoolean(key, getDefaultBoolValue(key));
    }

    public static boolean getBoolean(PreferenceKey key) {
        return getBoolean(key.name());
    }

    public static int getInt(PreferenceKey key) {
        return getPreferences().getInt(key.name(), getDefaultIntValue(key));
    }


    private static Integer getDefaultIntValue(PreferenceKey key) {
        return mapIntDefaults.get(key);
    }

    private static String getDefaultStringValue(PreferenceKey key) {
        return mapStringDefaults.getOrDefault(key, "");
    }

    public static Boolean getDefaultBoolValue(String key) {
        return mapBooleanDefaults.getOrDefault(PreferenceKey.valueOf(key), false);
    }


}
