package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.api.contracts.appdev.AppAuthInfo;
import com.mozu.mozuandroidinstoreassistant.app.tasks.AppAuthenticateAsyncTask;

public class AppNotAuthenticatedNoAuthTicket extends AppAuthenticationState implements AppAuthenticatorAsyncListener {

    private AppAuthInfo mAppAuthInfo;
    private String mBaseUrl;

    public AppNotAuthenticatedNoAuthTicket(AppAuthenticationStateMachine stateMachine, AppAuthInfo info, String baseUrl) {
        super(stateMachine);

        mAppAuthInfo = info;
        mBaseUrl = baseUrl;
    }

    @Override
    public void authenticateApp() {
        new AppAuthenticateAsyncTask(mAppAuthInfo, mBaseUrl, this).execute();
    }

    @Override
    public void appInitialized() {
        getStateMachine().setCurrentAppAuthState(getStateMachine().appAuthenticatedState);
    }

    @Override
    public void errorInitializingApp(String errorMessage) {
        getStateMachine().setCurrentAppAuthState(getStateMachine().appAuthenticationFailed);
        getStateMachine().appAuthenticationFailed.setErrorMessage(errorMessage);
    }
}
