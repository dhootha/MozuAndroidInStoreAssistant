package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import android.util.Log;

public abstract class AppAuthenticationState {

    private AppAuthenticationStateMachine mStateMachine;

    private String mErrorMessage;

    AppAuthenticationState(AppAuthenticationStateMachine stateMachine) {
        mStateMachine = stateMachine;
    }

    AppAuthenticationStateMachine getStateMachine() {
        return mStateMachine;
    }

    public void authenticateApp() {
        Log.d("AppAuthenticationStateMachine", "No Implementation For This State");
    }

    public boolean isErrorState() {
        return false;
    }

    public boolean isAuthenticatedState() {
        return false;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }
}
