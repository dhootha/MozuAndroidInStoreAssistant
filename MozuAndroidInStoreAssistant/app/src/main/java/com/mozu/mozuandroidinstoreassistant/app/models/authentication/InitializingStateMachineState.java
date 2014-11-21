package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.tasks.ObtainAuthProfileFromDiskAsyncTask;
import com.mozu.mozuandroidinstoreassistant.app.tasks.ReadUserPrefsFromDiskAsyncTask;

import java.util.ArrayList;
import java.util.List;

/*
    Class to determine which initial state the statemachine is in based on what
    is located on disk, be it, authentication profiles that need to be read up
    or tenants that need to be read up from user preferences

    1. First reads user preferences

    2. Then reads auth profiles
 */
public class InitializingStateMachineState extends UserAuthenticationState implements AuthProfileDiskInteractorListener, UserPreferencesDiskInteractorListener {

    public InitializingStateMachineState(UserAuthenticationStateMachine stateMachine) {
        super(stateMachine);

        new ReadUserPrefsFromDiskAsyncTask(this, getStateMachine().getContext()).execute();
    }

    @Override
    public void authProfileReadFromDisk(AuthenticationProfile profile) {
        if (profile == null) {

            getStateMachine().setCurrentUserAuthState(getStateMachine().userNotAuthenticatedNoAuthTicket);
        } else {

            getStateMachine().setAuthProfile(profile);
            getStateMachine().setCurrentUserAuthState(getStateMachine().userNotAuthenticatedAuthTicket);
            getStateMachine().authenticateUser();
        }
    }

    @Override
    public void failedToReadAuthProfile() {
        Log.d("Failed To Read Auth Profile", "Failed To Read Auth Profile");
        getStateMachine().setCurrentUserAuthState(getStateMachine().userNotAuthenticatedNoAuthTicket);
    }

    @Override
    public void finishedReading(List<UserPreferences> prefs) {
        getStateMachine().setAllUserPrefs(prefs);

        new ObtainAuthProfileFromDiskAsyncTask(this, getStateMachine().getContext()).execute();
    }

    @Override
    public void failedToWrite() {
        Crashlytics.log("error occurred while reading user not auth no auth ticket, most likely this occurred because one was never written to disk because user hasn't signed in yet: ");
    }

    @Override
    public void failedToReadUserPrefs() {
        Crashlytics.log("error occurred while reading user prefs ");

        getStateMachine().setAllUserPrefs(new ArrayList<UserPreferences>());

        new ObtainAuthProfileFromDiskAsyncTask(this, getStateMachine().getContext()).execute();
    }

    @Override
    public boolean isLoadingState() {
        return true;
    }
}
