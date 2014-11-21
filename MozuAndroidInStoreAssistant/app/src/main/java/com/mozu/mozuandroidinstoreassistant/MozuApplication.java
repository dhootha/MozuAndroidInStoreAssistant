package com.mozu.mozuandroidinstoreassistant;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.mozu.mozuandroidinstoreassistant.app.BuildConfig;

public class MozuApplication extends Application  {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ENABLE_CRASHLYTICS) {
            Crashlytics.start(this);
        }
    }
}
