package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.ApiException;
import com.mozu.api.contracts.core.UserAuthInfo;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.api.security.AuthenticationScope;
import com.mozu.api.security.UserAuthenticator;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticatorAsyncListener;

public class UserAuthenticateAsyncTask extends AsyncTask<Void, Void, AuthenticationProfile> {

    private UserAuthInfo mAuthInfo;
    private UserAuthenticatorAsyncListener mLoginListener;

    public UserAuthenticateAsyncTask(UserAuthInfo authInfo, UserAuthenticatorAsyncListener loginListener) {
        mAuthInfo = authInfo;
        mLoginListener = loginListener;
    }

    @Override
    public AuthenticationProfile doInBackground(Void... params) {

        try {
            return UserAuthenticator.authenticate(mAuthInfo, AuthenticationScope.Tenant, null);
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
            mLoginListener.userAuthenticated(profile);
        } else {
            mLoginListener.authenticationFailed("");
        }
    }

    @Override
    public void onCancelled() {
        mLoginListener.authenticationFailed("");
    }
}