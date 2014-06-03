package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.security.AuthTicket;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.api.security.UserAuthenticator;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.RefreshAuthProfileListener;

public class RefreshAuthProfileAsyncTask extends InternetConnectedAsyncTask<Void, Void, AuthenticationProfile> {

    private RefreshAuthProfileListener mListener;
    private AuthTicket mAuthTicket;

    public RefreshAuthProfileAsyncTask(Context context, RefreshAuthProfileListener listener, AuthTicket authTicket) {
        super(context);

        mListener = listener;
        mAuthTicket = authTicket;

    }

    @Override
    protected AuthenticationProfile doInBackground(Void... params) {
        super.doInBackground(params);

        if (mAuthTicket == null) {
            return null;
        }

        AuthenticationProfile profile = null;

        try {
            profile = UserAuthenticator.refreshUserAuthTicket(mAuthTicket);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        return profile;
    }

    @Override
    protected void onPostExecute(AuthenticationProfile profile) {

        mListener.authProfileRefreshed(profile);
    }
}
