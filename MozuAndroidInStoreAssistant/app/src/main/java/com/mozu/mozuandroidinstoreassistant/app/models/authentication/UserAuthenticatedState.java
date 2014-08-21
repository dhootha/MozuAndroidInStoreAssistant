package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import android.util.Log;

import com.mozu.api.security.AuthenticationProfile;
import com.mozu.api.security.AuthenticationScope;
import com.mozu.api.security.Scope;
import com.mozu.api.security.UserAuthenticator;
import com.mozu.mozuandroidinstoreassistant.app.serialization.CurrentAuthTicketSerializer;
import com.mozu.mozuandroidinstoreassistant.app.tasks.LogoutUserAsyncTask;
import com.mozu.mozuandroidinstoreassistant.app.tasks.UpdateScopeAsyncTask;

public class UserAuthenticatedState extends UserAuthenticationState implements UpdateTenantInfoListener {


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

        getStateMachine().updateUserPreferences();

        getStateMachine().setCurrentUserAuthState(getStateMachine().userNotAuthenticatedNoAuthTicket);
        getStateMachine().resetTenantSiteId();
    }

    @Override
    public void updateScope(Scope scope) {

        new UpdateScopeAsyncTask(getStateMachine().getContext(), getStateMachine().getAuthProfile(), this, scope).execute();
    }

    @Override
    public void updateTenantFinished(AuthenticationProfile profile) {

        getStateMachine().setAuthProfile(profile);
        getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticatedTenantSet);
    }

    @Override
    public void updateTenantFailed() {

        Log.d("tenant update failed", "nothing to do");
    }

}
