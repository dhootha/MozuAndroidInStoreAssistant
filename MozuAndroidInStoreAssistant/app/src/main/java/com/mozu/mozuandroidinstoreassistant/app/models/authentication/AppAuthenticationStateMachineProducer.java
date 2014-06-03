package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import android.content.Context;

import com.mozu.api.contracts.appdev.AppAuthInfo;

public class AppAuthenticationStateMachineProducer {

    private static AppAuthenticationStateMachine mStateMachine;

    public static AppAuthenticationStateMachine getInstance(Context context, AppAuthInfo info, String baseUrl) {
        if (mStateMachine == null) {
            mStateMachine = new AppAuthenticationStateMachine(context, info, baseUrl);
        }

        return mStateMachine;
    }

}
