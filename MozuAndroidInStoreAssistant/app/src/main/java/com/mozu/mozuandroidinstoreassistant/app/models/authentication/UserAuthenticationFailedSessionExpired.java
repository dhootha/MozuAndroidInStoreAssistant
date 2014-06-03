package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

public class UserAuthenticationFailedSessionExpired extends UserNotAuthenticatedNoAuthTicket {

    public UserAuthenticationFailedSessionExpired(UserAuthenticationStateMachine stateMachine) {
        super(stateMachine);
    }

    @Override
    public boolean isErrorState() {
        return true;
    }

}
