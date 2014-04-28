package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.mozuandroidinstoreassistant.app.serialization.CurrentAuthTicketSerializer;
import com.mozu.mozuandroidinstoreassistant.app.tasks.LogoutUserAsyncTask;

public class UserAuthenticatedState extends UserAuthenticationState {


    public UserAuthenticatedState(UserAuthenticationStateMachine stateMachine) {
        super(stateMachine);
    }

    @Override
    public boolean isAuthenticatedState() {
        if (!getStateMachine().isAuthProfileStillValid()) {
            CurrentAuthTicketSerializer authTicketStore = new CurrentAuthTicketSerializer(getStateMachine().getContext());
            authTicketStore.deleteFile();

            getStateMachine().setCurrentUserAuthState(getStateMachine().userNotAuthenticatedNoAuthTicket);
            getStateMachine().authenticateUser();

            return false;
        }

        //refresh auth ticket
        getStateMachine().refreshAuthProfile();

        return true;
    }

    @Override
    public void signOutUser() {
        new LogoutUserAsyncTask(getStateMachine().getAuthProfile().getAuthTicket()).execute();

        CurrentAuthTicketSerializer authTicket = new CurrentAuthTicketSerializer(getStateMachine().getContext());
        authTicket.deleteFile();

        getStateMachine().setCurrentUserAuthState(getStateMachine().userNotAuthenticatedNoAuthTicket);
    }
}
