package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

public class AppAuthenticated extends AppAuthenticationState {

    public AppAuthenticated(AppAuthenticationStateMachine stateMachine) {
        super(stateMachine);
    }

    @Override
    public boolean isAuthenticatedState() {
        return true;
    }
}
