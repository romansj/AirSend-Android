package com.cherrydev.airsend.app.settings;

public class Preference<T> {

    private String id;
    private T defaultValue;

    private PreferenceType type;

    public enum PreferenceType {
        STRING,
        BOOL,
        INT,
        LONG,
        FLOAT
    }

    public Preference(String id, T defaultValue, PreferenceType type) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public PreferenceType getType() {
        return type;
    }
}
