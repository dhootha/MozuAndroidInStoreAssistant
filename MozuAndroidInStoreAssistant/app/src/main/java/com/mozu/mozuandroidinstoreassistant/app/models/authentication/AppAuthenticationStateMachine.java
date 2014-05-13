package com.mozu.mozuandroidinstoreassistant.app.models.authentication;


import com.mozu.api.contracts.appdev.AppAuthInfo;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;

import java.util.Observable;

public class AppAuthenticationStateMachine extends Observable {

    private AppAuthenticationState mCurrentAppAuthState;

    protected AppAuthenticated appAuthenticatedState;
    protected AppNotAuthenticatedNoAuthTicket appNotAuthenticatedNoAuthTicket;
    protected AppAuthenticationFailed appAuthenticationFailed;

    public AppAuthenticationStateMachine(AppAuthInfo info, String baseUrl) {
        appAuthenticatedState = new AppAuthenticated(this);
        appNotAuthenticatedNoAuthTicket = new AppNotAuthenticatedNoAuthTicket(this, info, baseUrl);
        appAuthenticationFailed = new AppAuthenticationFailed(this, info, baseUrl);

        mCurrentAppAuthState = appNotAuthenticatedNoAuthTicket;
    }

    protected void setCurrentAppAuthState(AppAuthenticationState appAuthState) {
        mCurrentAppAuthState = appAuthState;

        setChanged();
        notifyObservers();
    }

    public AppAuthenticationState getCurrentAppAuthState() {
        return mCurrentAppAuthState;
    }

    public void authenticateApp() {
        mCurrentAppAuthState.authenticateApp();
    }

}
