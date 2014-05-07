package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import android.content.Context;

public class UserAuthenticationStateMachineProducer {

    private static UserAuthenticationStateMachine mStateMachine;

    public static UserAuthenticationStateMachine getInstance(Context context) {
        if (mStateMachine == null) {
            mStateMachine = new UserAuthenticationStateMachine(context);
        }

        return mStateMachine;
    }

}
