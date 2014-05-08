package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.tasks.ObtainAuthProfileFromDiskAsyncTask;
import com.mozu.mozuandroidinstoreassistant.app.tasks.UserAuthenticateAsyncTask;

public class UserNotAuthenticatedNoAuthTicket extends UserAuthenticationState implements UserAuthenticatorAsyncListener {

    public UserNotAuthenticatedNoAuthTicket(UserAuthenticationStateMachine stateMachine) {
        super(stateMachine);

        new ObtainAuthProfileFromDiskAsyncTask(this, getStateMachine().getContext()).execute();
    }

    @Override
    public void authenticateUser() {

        new UserAuthenticateAsyncTask(getStateMachine().getUserAuthInfo(), this).execute();
    }

    @Override
    public void userAuthenticated(AuthenticationProfile profile) {
        if (profile == null) {
            Crashlytics.log("Unexpected that Authentication Profile would be null, Authentication must have failed");
            getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticationFailed);

            return;
        }

        getStateMachine().setAuthProfile(profile);

        if (profile.getActiveScope() != null)  {

            UserPreferences pref = getStateMachine().getCurrentUsersPreferences();
            pref.setDefaultTenantId(String.valueOf(profile.getActiveScope().getId()));
            getStateMachine().updateUserPreferences();

            getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticatedTenantSet);
        } else {

            getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticatedNoTenantSet);
        }
    }

    @Override
    public void authenticationFailed(String errorMessage) {

        getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticationFailed);
    }

    @Override
    public void authProfileReadFromDisk(AuthenticationProfile profile) {
        if (profile == null)
            return;

        getStateMachine().setAuthProfile(profile);
        getStateMachine().setCurrentUserAuthState(getStateMachine().userNotAuthenticatedAuthTicket);
        getStateMachine().authenticateUser();
    }

    @Override
    public void errored(String errorMessage) {

        Crashlytics.log("error occurred while reading user not auth no auth ticket, most likely this occurred because one was never written to disk because user hasn't signed in yet: " + errorMessage);
    }
}
