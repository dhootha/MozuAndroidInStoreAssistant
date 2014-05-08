package com.mozu.mozuandroidinstoreassistant.app.models.authentication;;

public class UserAuthenticatedTenantSet extends UserAuthenticatedState {

    public UserAuthenticatedTenantSet(UserAuthenticationStateMachine stateMachine) {
        super(stateMachine);
    }

    public boolean isTenantSelectedState() {
        return true;
    }

}
