package com.mozu.mozuandroidinstoreassistant;

import android.app.Application;
import android.support.v4.util.ArrayMap;

import com.crashlytics.android.Crashlytics;
import com.mozu.MozuAndroid;
import com.mozu.mozuandroidinstoreassistant.app.BuildConfig;

import io.fabric.sdk.android.Fabric;

public class MozuApplication extends Application {

    private ArrayMap<String, String> mLocations = new ArrayMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ENABLE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }
        MozuAndroid.initialize(this);
    }


    public void setLocations(ArrayMap<String, String> locations) {
        mLocations = locations;
    }

    public ArrayMap<String, String> getLocations() {
        return mLocations;
    }


}
