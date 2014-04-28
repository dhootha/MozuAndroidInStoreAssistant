package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.api.contracts.appdev.AppAuthInfo;

public class AppAuthenticationFailed extends AppNotAuthenticatedNoAuthTicket {

    public AppAuthenticationFailed(AppAuthenticationStateMachine stateMachine, AppAuthInfo info, String baseUrl) {
        super(stateMachine, info, baseUrl);
    }

    @Override
    public void authenticateApp() {
        super.authenticateApp();
    }

    @Override
    public boolean isErrorState() {
        return true;
    }
}
