package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.ApiException;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.api.security.AuthenticationScope;
import com.mozu.api.security.Scope;
import com.mozu.api.security.UserAuthenticator;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UpdateTenantInfoListener;

public class UpdateScopeAsyncTask extends InternetConnectedAsyncTask<Void, Void, AuthenticationProfile> {

    private AuthenticationProfile mAuthProfile;
    private UpdateTenantInfoListener mUpdateListener;
    private Scope mNewScope;

    public UpdateScopeAsyncTask(Context context, AuthenticationProfile authProfile, UpdateTenantInfoListener updateListener, Scope newScope) {
        super(context);

        mAuthProfile = authProfile;
        mUpdateListener = updateListener;
        mNewScope = newScope;
    }

    @Override
    public AuthenticationProfile doInBackground(Void... params) {
        super.doInBackground(params);

        try {
            mAuthProfile.getAuthTicket().setScope(AuthenticationScope.Tenant);
            return UserAuthenticator.refreshUserAuthTicket(mAuthProfile.getAuthTicket(), mNewScope.getId());
        } catch (ApiException e) {

            Crashlytics.logException(e);
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return null;
    }

    @Override
    public void onPostExecute(AuthenticationProfile profile) {

        if (profile != null) {
            mUpdateListener.updateTenantFinished(profile);
        } else {
            mUpdateListener.updateTenantFailed();
        }
    }

    @Override
    public void onCancelled() {
        mUpdateListener.updateTenantFailed();
    }
}