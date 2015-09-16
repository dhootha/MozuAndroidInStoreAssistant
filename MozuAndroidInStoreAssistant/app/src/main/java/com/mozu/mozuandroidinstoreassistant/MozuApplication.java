package com.mozu.mozuandroidinstoreassistant;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v4.util.ArrayMap;

import com.crashlytics.android.Crashlytics;
import com.mozu.MozuAndroid;
import com.mozu.mozuandroidinstoreassistant.app.BuildConfig;

import io.fabric.sdk.android.Fabric;

public class MozuApplication extends Application {

    private static final String MODE_PREFERENCE = "mode";
    private ArrayMap<String, String> mLocations = new ArrayMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ENABLE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }
        MozuAndroid.initialize(this);
    }

    public ArrayMap<String, String> getLocations() {
        return mLocations;
    }

    public void setLocations(ArrayMap<String, String> locations) {
        mLocations = locations;
    }

    public boolean isAdminMode() {
        SharedPreferences preferences = getSharedPreferences(MODE_PREFERENCE, MODE_MULTI_PROCESS);
        return preferences.getBoolean(MODE_PREFERENCE, false);
    }

    public void setIsAdminMode(boolean isAdminMode) {
        SharedPreferences preferences = getSharedPreferences(MODE_PREFERENCE, MODE_MULTI_PROCESS);
        preferences.edit().putBoolean(MODE_PREFERENCE, isAdminMode).apply();
    }
}
