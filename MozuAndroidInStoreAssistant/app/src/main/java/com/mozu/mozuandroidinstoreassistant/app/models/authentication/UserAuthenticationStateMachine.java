package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import android.content.Context;
import android.util.Log;

import com.mozu.api.contracts.core.UserAuthInfo;
import com.mozu.api.contracts.tenant.Site;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.api.security.Scope;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.tasks.RefreshAuthProfileAsyncTask;
import com.mozu.mozuandroidinstoreassistant.app.tasks.WriteAuthProfileToDiskAsyncTask;
import com.mozu.mozuandroidinstoreassistant.app.tasks.WriteUserPrefsFromDiskAsyncTask;

import java.util.List;
import java.util.Observable;

public class UserAuthenticationStateMachine extends Observable implements RefreshAuthProfileListener, UserPreferencesDiskInteractorListener {

    private UserAuthenticationState mCurrentUserAuthState;
    private Context mContext;
    private AuthenticationProfile mAuthProfile;
    private UserAuthInfo mUserAuthInfo;

    protected InitializingStateMachineState initializingStateMachineState;
    protected UserAuthenticatedTenantSet userAuthenticatedTenantSet;
    protected UserAuthenticatedNoTenantSet userAuthenticatedNoTenantSet;
    protected UserNotAuthenticatedAuthTicket userNotAuthenticatedAuthTicket;
    protected UserNotAuthenticatedNoAuthTicket userNotAuthenticatedNoAuthTicket;
    protected UserAuthenticationFailed userAuthenticationFailed;

    private List<UserPreferences> mAllUsersPrefs;

    public UserAuthenticationStateMachine(Context context) {
        mContext = context;

        mUserAuthInfo = new UserAuthInfo();

        initializingStateMachineState = new InitializingStateMachineState(this);
        userAuthenticatedTenantSet = new UserAuthenticatedTenantSet(this);
        userAuthenticatedNoTenantSet = new UserAuthenticatedNoTenantSet(this);
        userNotAuthenticatedAuthTicket = new UserNotAuthenticatedAuthTicket(this);
        userNotAuthenticatedNoAuthTicket = new UserNotAuthenticatedNoAuthTicket(this);
        userAuthenticationFailed = new UserAuthenticationFailed(this);

        mCurrentUserAuthState = initializingStateMachineState;
    }

    protected void setCurrentUserAuthState(UserAuthenticationState userAuthState) {
        mCurrentUserAuthState = userAuthState;

        if (mCurrentUserAuthState.isAuthenticatedState()) {
            updateUserPreferences();
        }

        setChanged();
        notifyObservers();
    }

    public UserAuthenticationState getCurrentUserAuthState() {

        return mCurrentUserAuthState;
    }

    public void authenticateUser() {

        mCurrentUserAuthState.authenticateUser();
    }

    public void updateScope(Scope scope) {
        //update scope on preferences
        UserPreferences prefs = getCurrentUsersPreferences();
        prefs.setDefaultTenantId(String.valueOf(scope.getId()));

        mCurrentUserAuthState.updateScope(scope);
    }

    protected Context getContext() {

        return mContext;
    }

    public AuthenticationProfile getAuthProfile() {

        return mAuthProfile;
    }

    protected void setAuthProfile(AuthenticationProfile profile) {
        //save active scope if one exists
        mAuthProfile = profile;

        if (profile.getActiveScope() != null) {
            getCurrentUsersPreferences().setDefaultTenantId(String.valueOf(profile.getActiveScope().getId()));
        }

        updateUserPreferences();

        new WriteAuthProfileToDiskAsyncTask(mAuthProfile, getContext()).execute();
    }

    protected UserAuthInfo getUserAuthInfo() {

        return mUserAuthInfo;
    }

    public void setUserAuthInfo(UserAuthInfo info) {

        mUserAuthInfo = info;
    }

    protected void refreshAuthProfile() {
        //return because auth profile cannot be refreshed
        if (mAuthProfile == null || mAuthProfile.getAuthTicket() == null || mAuthProfile.getAuthTicket().getAccessTokenExpiration() == null || !mAuthProfile.getAuthTicket().getAccessTokenExpiration().isAfterNow()) {
            return;
        }

        new RefreshAuthProfileAsyncTask(this, mAuthProfile.getAuthTicket()).execute();
    }

    @Override
    public void authProfileRefreshed(AuthenticationProfile profile) {
        setAuthProfile(profile);
    }

    protected boolean isAuthProfileStillValid() {

        return getAuthProfile() != null &&
               getAuthProfile().getAuthTicket() != null &&
               getAuthProfile().getAuthTicket().getAccessTokenExpiration() != null &&
               getAuthProfile().getAuthTicket().getAccessTokenExpiration().isAfterNow();
    }

    public void updateUserPreferences() {
        new WriteUserPrefsFromDiskAsyncTask(this, getContext(), mAllUsersPrefs).execute();
    }

    public UserPreferences getCurrentUsersPreferences() {

        for (UserPreferences prefs: mAllUsersPrefs) {
            if (prefs.getEmail().equalsIgnoreCase(mAuthProfile.getUserProfile().getEmailAddress())) {
                return prefs;
            }
        }

        //didn't find prefs for this user, create new one and add it
        UserPreferences prefs = new UserPreferences();
        prefs.setEmail(mAuthProfile.getUserProfile().getEmailAddress());

        mAllUsersPrefs.add(prefs);

        return prefs;
    }

    @Override
    public void finishedReading(List<UserPreferences> prefs) {
        mAllUsersPrefs = prefs;
    }

    protected void setAllUserPrefs(List<UserPreferences> prefs) {
        mAllUsersPrefs = prefs;
    }

    public List<UserPreferences> getAllUserPrefs() {

        return mAllUsersPrefs;
    }

    @Override
    public void failedToWrite() {
        Log.d("failed to write user prefs", "failed to write user prefs");
    }

    @Override
    public void failedToReadUserPrefs() {
        Log.d("failed to read user prefs", "failed to read user prefs");
    }

    public void setCurrentSite(Site site) {
        getCurrentUsersPreferences().setDefaultSiteId(String.valueOf(site.getId()));

        updateUserPreferences();
    }
}
