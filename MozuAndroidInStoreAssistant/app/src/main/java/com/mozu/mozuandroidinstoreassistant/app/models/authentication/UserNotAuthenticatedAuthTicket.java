package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.mozuandroidinstoreassistant.app.serialization.CurrentAuthTicketSerializer;

public class UserNotAuthenticatedAuthTicket extends UserAuthenticationState {

    public UserNotAuthenticatedAuthTicket(UserAuthenticationStateMachine stateMachine) {
        super(stateMachine);
    }

    @Override
    public void authenticateUser() {
        //if auth ticket doesnt exist or is expired
        if (!getStateMachine().isAuthProfileStillValid()) {
            CurrentAuthTicketSerializer authTicketStore = new CurrentAuthTicketSerializer(getStateMachine().getContext());
            authTicketStore.deleteFile();

            getStateMachine().setCurrentUserAuthState(getStateMachine().userNotAuthenticatedNoAuthTicket);

            return;
        }

        //if auth ticket is still good, then use it and set state machine
        if (getStateMachine().getAuthProfile().getActiveScope() != null) {
            getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticatedTenantSet);
        } else {
            getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticatedNoTenantSet);
        }

        //refresh auth ticket
        getStateMachine().refreshAuthProfile();
    }

}
