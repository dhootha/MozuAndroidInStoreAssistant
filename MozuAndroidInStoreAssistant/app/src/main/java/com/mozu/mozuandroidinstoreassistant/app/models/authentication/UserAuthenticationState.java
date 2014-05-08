package com.mozu.mozuandroidinstoreassistant.app.models.authentication;


import android.util.Log;

import com.mozu.api.security.Scope;

public class UserAuthenticationState {

    private UserAuthenticationStateMachine mStateMachine;

    private String mErrorMessage;

    public UserAuthenticationState(UserAuthenticationStateMachine stateMachine) {
        mStateMachine = stateMachine;
    }

    protected UserAuthenticationStateMachine getStateMachine() {
        return mStateMachine;
    }

    public void authenticateUser() {
        Log.d("UserAuthenticationStateMachine", "No Implementation For This State");
    }

    public void signOutUser() {
        Log.d("UserAuthenticationStateMachine", "No Implementation For This State");
    }

    public void updateScope(Scope scope) {
        Log.d("UserAuthenticationStateMachine", "No Implementation For This State");
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

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    public boolean isTenantSelectedState() {
        return false;
    }
}
