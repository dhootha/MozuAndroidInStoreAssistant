package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.tasks.UserAuthenticateAsyncTask;

public class UserNotAuthenticatedNoAuthTicket extends UserAuthenticationState implements UserAuthenticatorAsyncListener {

    public UserNotAuthenticatedNoAuthTicket(UserAuthenticationStateMachine stateMachine) {
        super(stateMachine);
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

        UserPreferences pref = getStateMachine().getCurrentUsersPreferences();

        if (profile.getActiveScope() != null) {

            pref.setDefaultTenantId(String.valueOf(profile.getActiveScope().getId()));
            getStateMachine().updateUserPreferences();

            getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticatedTenantSet);
        } else if (pref.getDontAskToSetTenantSiteIfSet() && pref.getDefaultTenantId() != null && !pref.getDefaultTenantId().equalsIgnoreCase("") && !pref.getDefaultTenantId().equalsIgnoreCase("null")) {

            getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticatedTenantSet);
        } else {

            getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticatedNoTenantSet);
        }
    }

    @Override
    public void authenticationFailed(String errorMessage) {

        getStateMachine().setCurrentUserAuthState(getStateMachine().userAuthenticationFailed);
    }

}
