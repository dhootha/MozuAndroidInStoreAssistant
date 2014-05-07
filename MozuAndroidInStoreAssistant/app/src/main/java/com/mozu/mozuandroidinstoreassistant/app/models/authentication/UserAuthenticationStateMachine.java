package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import android.content.Context;

import com.mozu.api.contracts.core.UserAuthInfo;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.mozuandroidinstoreassistant.app.tasks.RefreshAuthProfileAsyncTask;
import com.mozu.mozuandroidinstoreassistant.app.tasks.WriteAuthProfileToDiskAsyncTask;

import java.util.Observable;

public class UserAuthenticationStateMachine extends Observable implements RefreshAuthProfileListener {

    private UserAuthenticationState mCurrentUserAuthState;
    private Context mContext;
    private AuthenticationProfile mAuthProfile;
    private UserAuthInfo mUserAuthInfo;

    protected UserAuthenticatedTenantSet userAuthenticatedTenantSet;
    protected UserAuthenticatedNoTenantSet userAuthenticatedNoTenantSet;
    protected UserNotAuthenticatedAuthTicket userNotAuthenticatedAuthTicket;
    protected UserNotAuthenticatedNoAuthTicket userNotAuthenticatedNoAuthTicket;
    protected UserAuthenticationFailed userAuthenticationFailed;


    public UserAuthenticationStateMachine(Context context) {
        mContext = context;
        mUserAuthInfo = new UserAuthInfo();

        userAuthenticatedTenantSet = new UserAuthenticatedTenantSet(this);
        userAuthenticatedNoTenantSet = new UserAuthenticatedNoTenantSet(this);
        userNotAuthenticatedAuthTicket = new UserNotAuthenticatedAuthTicket(this);
        userNotAuthenticatedNoAuthTicket = new UserNotAuthenticatedNoAuthTicket(this);
        userAuthenticationFailed = new UserAuthenticationFailed(this);

        mCurrentUserAuthState = userNotAuthenticatedNoAuthTicket;
    }

    protected void setCurrentUserAuthState(UserAuthenticationState userAuthState) {
        mCurrentUserAuthState = userAuthState;

        setChanged();
        notifyObservers();
    }

    public UserAuthenticationState getCurrentUserAuthState() {

        return mCurrentUserAuthState;
    }

    public void authenticateUser() {

        mCurrentUserAuthState.authenticateUser();
    }

    protected Context getContext() {

        return mContext;
    }

    protected AuthenticationProfile getAuthProfile() {

        return mAuthProfile;
    }

    protected void setAuthProfile(AuthenticationProfile profile) {

        mAuthProfile = profile;

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
}
