package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

public class UserAuthenticationFailed extends UserNotAuthenticatedNoAuthTicket {

    public UserAuthenticationFailed(UserAuthenticationStateMachine stateMachine) {
        super(stateMachine);
    }

    @Override
    public boolean isErrorState() {
        return true;
    }

}
