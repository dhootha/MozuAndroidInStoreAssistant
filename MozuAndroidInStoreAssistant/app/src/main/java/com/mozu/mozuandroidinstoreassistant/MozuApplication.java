package com.mozu.mozuandroidinstoreassistant;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.mozu.mozuandroidinstoreassistant.app.BuildConfig;

import io.fabric.sdk.android.Fabric;

public class MozuApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ENABLE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }
    }
}
