package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.api.contracts.appdev.AppAuthInfo;

public class AppAuthenticationStateMachineProducer {

    private static AppAuthenticationStateMachine mStateMachine;

    public static AppAuthenticationStateMachine getInstance(AppAuthInfo info, String baseUrl) {
        if (mStateMachine == null) {
            mStateMachine = new AppAuthenticationStateMachine(info, baseUrl);
        }

        return mStateMachine;
    }

}
