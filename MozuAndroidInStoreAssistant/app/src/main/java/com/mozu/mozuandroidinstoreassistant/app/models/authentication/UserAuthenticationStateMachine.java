package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mozu.api.contracts.core.UserAuthInfo;
import com.mozu.api.contracts.location.Location;
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

    protected InitializingStateMachineState initializingStateMachineState;
    protected UserAuthenticatedTenantSet userAuthenticatedTenantSet;
    protected UserAuthenticatedNoTenantSet userAuthenticatedNoTenantSet;
    protected UserNotAuthenticatedAuthTicket userNotAuthenticatedAuthTicket;
    protected UserNotAuthenticatedNoAuthTicket userNotAuthenticatedNoAuthTicket;
    protected UserAuthenticationFailed userAuthenticationFailed;
    protected UserAuthenticationFailedSessionExpired userAuthenticationFailedSessionExpired;
    private UserAuthenticationState mCurrentUserAuthState;
    private Context mContext;
    private AuthenticationProfile mAuthProfile;
    private UserAuthInfo mUserAuthInfo;
    private List<UserPreferences> mAllUsersPrefs;
    private Integer mTenantId;
    private String mTenantName;
    private Integer mSiteId;
    private String mSiteName;
    private String mSiteDomain;
    private Location mLocation;
    private String mLocationName;
    private String mLocationCode;


    protected UserAuthenticationStateMachine(Context context) {
        mContext = context;

        mUserAuthInfo = new UserAuthInfo();

        initializingStateMachineState = new InitializingStateMachineState(this);
        userAuthenticatedTenantSet = new UserAuthenticatedTenantSet(this);
        userAuthenticatedNoTenantSet = new UserAuthenticatedNoTenantSet(this);
        userNotAuthenticatedAuthTicket = new UserNotAuthenticatedAuthTicket(this);
        userNotAuthenticatedNoAuthTicket = new UserNotAuthenticatedNoAuthTicket(this);
        userAuthenticationFailed = new UserAuthenticationFailed(this);
        userAuthenticationFailedSessionExpired = new UserAuthenticationFailedSessionExpired(this);

        mCurrentUserAuthState = initializingStateMachineState;
    }

    public UserAuthenticationState getCurrentUserAuthState() {

        return mCurrentUserAuthState;
    }

    protected void setCurrentUserAuthState(UserAuthenticationState userAuthState) {
        mCurrentUserAuthState = userAuthState;
        if (mCurrentUserAuthState.isAuthenticatedState()) {
            updateUserPreferences();
        }

        setChanged();
        notifyObservers();
    }

    public void authenticateUser() {

        mCurrentUserAuthState.authenticateUser();
    }

    public void updateScope(Scope scope) {
        setTenantId(scope);
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

        if (profile != null && profile.getActiveScope() != null) {
            setTenantId(profile.getActiveScope());
        }

        updateUserPreferences();

        new WriteAuthProfileToDiskAsyncTask(mAuthProfile, getContext()).execute();
    }

    public UserAuthInfo getUserAuthInfo() {

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

        new RefreshAuthProfileAsyncTask(getContext(), this, mAuthProfile.getAuthTicket()).execute();
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
            if (prefs != null && prefs.getEmail() != null && mAuthProfile != null && mAuthProfile.getUserProfile() != null && prefs.getEmail().equalsIgnoreCase(mAuthProfile.getUserProfile().getEmailAddress())) {
                return prefs;
            }
        }

        //didn't find prefs for this user, create new one and add it
        UserPreferences prefs = new UserPreferences();
        if (mAuthProfile != null && mAuthProfile.getUserProfile() != null) {
            prefs.setEmail(mAuthProfile.getUserProfile().getEmailAddress());
            mAllUsersPrefs.add(prefs);
        }

        return prefs;
    }

    @Override
    public void finishedReading(List<UserPreferences> prefs) {
        mAllUsersPrefs = prefs;
    }

    public List<UserPreferences> getAllUserPrefs() {

        return mAllUsersPrefs;
    }

    protected void setAllUserPrefs(List<UserPreferences> prefs) {
        mAllUsersPrefs = prefs;
    }

    @Override
    public void failedToWrite() {
        Log.d("failed to write user prefs", "failed to write user prefs");
    }

    @Override
    public void failedToReadUserPrefs() {
        Log.d("failed to read user prefs", "failed to read user prefs");
    }

    public void setCurrentSiteId(Site site) {
        if (site != null) {
            mSiteId = site.getId();
            mSiteDomain = site.getDomain();
            mSiteName = site.getName();
        } else {
            mSiteId = null;
            mSiteDomain = null;
            mSiteName = null;
        }
    }

    public void setCurrentLocation(Location location) {
        if (location != null) {
            mLocation = location;
            mLocationCode = location.getCode();
            mLocationName = location.getName();
        }
    }

    public void persistSiteTenantId(){
        getCurrentUsersPreferences().setDefaultTenantId(String.valueOf(mTenantId));
        getCurrentUsersPreferences().setDefaultSiteId(String.valueOf(mSiteId));
        getCurrentUsersPreferences().setDefaultSiteDomain(mSiteDomain);
        getCurrentUsersPreferences().setDefaultSiteName(mSiteName);
        getCurrentUsersPreferences().setDefaultTenantName(mTenantName);
        getCurrentUsersPreferences().setDefaultLocationId(mLocationCode);
        getCurrentUsersPreferences().setDefaultLocationName(mLocationName);

        updateUserPreferences();
    }

    public Integer getTenantId(){
        if (mTenantId == null) {
            if (!TextUtils.isEmpty(getCurrentUsersPreferences().getDefaultTenantId()) && !getCurrentUsersPreferences().getDefaultTenantId().equalsIgnoreCase("null")) {
                return Integer.parseInt(getCurrentUsersPreferences().getDefaultTenantId());
            }
        }
        return mTenantId;
    }

    public void setTenantId(Scope scope) {
        if (scope != null) {
            mTenantId = scope.getId();
            mTenantName = scope.getName();
        } else {
            mTenantId = null;
            mTenantName = null;
        }
    }

    public String getTenantName(){
        if (!TextUtils.isEmpty(getCurrentUsersPreferences().getDefaultTenantName()) && !getCurrentUsersPreferences().getDefaultTenantName().equalsIgnoreCase("null")) {
            return getCurrentUsersPreferences().getDefaultTenantName();
        }else{
            return null;
        }
    }


    public Integer getSiteId(){
        if (mSiteId == null) {
            if (!TextUtils.isEmpty(getCurrentUsersPreferences().getDefaultSiteId()) && !getCurrentUsersPreferences().getDefaultSiteId().equalsIgnoreCase("null")) {
                return Integer.parseInt(getCurrentUsersPreferences().getDefaultSiteId());
            }
        }
        return mSiteId;
    }

    public String getSiteName() {
        if (!TextUtils.isEmpty(getCurrentUsersPreferences().getDefaultSiteName()) && !getCurrentUsersPreferences().getDefaultSiteName().equalsIgnoreCase("null")) {
            return getCurrentUsersPreferences().getDefaultSiteName();
        } else {
            return null;
        }
    }

    public String getSiteDomain(){
        if (mSiteDomain == null) {
            if (!TextUtils.isEmpty(getCurrentUsersPreferences().getDefaultSiteDomain()) && !getCurrentUsersPreferences().getDefaultSiteDomain().equalsIgnoreCase("null")) {
                return getCurrentUsersPreferences().getDefaultSiteDomain();
            }
        }
        return mSiteDomain;
    }

    public void resetTenantSiteId(){
        mSiteId = null;
        mTenantId = null;
    }
}
