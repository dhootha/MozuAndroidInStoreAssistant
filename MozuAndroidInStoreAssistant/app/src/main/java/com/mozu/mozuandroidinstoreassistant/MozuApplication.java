package com.mozu.mozuandroidinstoreassistant;

import android.app.Application;
import android.support.v4.util.ArrayMap;

import com.crashlytics.android.Crashlytics;
import com.mozu.MozuAndroid;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.mozuandroidinstoreassistant.app.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MozuApplication extends Application {

    private ArrayMap<String, String> mLocations = new ArrayMap<>();
    private HashMap<String, List<Payment>> mMockPayments = new HashMap<>();
    private boolean isAdminMode = false;

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
        return isAdminMode;
    }

    public void setIsAdminMode(boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
    }

    public HashMap<String, List<Payment>> getMockPayments() {
        return mMockPayments;
    }

    public HashMap<String, List<Payment>> addMockPayments(String orderId, List<Payment> payments, boolean deleteOlder) {
        if (!mMockPayments.containsKey(orderId) || deleteOlder) {
            mMockPayments.put(orderId, payments);
        } else {
            List<Payment> paymentList = new ArrayList<>();
            paymentList.addAll(payments);
            mMockPayments.put(orderId, paymentList);
        }
        return mMockPayments;
    }


}
