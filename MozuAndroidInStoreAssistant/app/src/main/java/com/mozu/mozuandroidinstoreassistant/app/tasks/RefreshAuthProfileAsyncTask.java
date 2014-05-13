package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.mozu.api.security.AuthTicket;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.api.security.UserAuthenticator;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.RefreshAuthProfileListener;

public class RefreshAuthProfileAsyncTask extends AsyncTask<Void, Void, AuthenticationProfile> {

    private RefreshAuthProfileListener mListener;
    private AuthTicket mAuthTicket;

    public RefreshAuthProfileAsyncTask(RefreshAuthProfileListener listener, AuthTicket authTicket) {

        mListener = listener;
        mAuthTicket = authTicket;

    }

    @Override
    protected AuthenticationProfile doInBackground(Void... params) {
        if (mAuthTicket == null) {
            return null;
        }

        return UserAuthenticator.refreshUserAuthTicket(mAuthTicket);
    }

    @Override
    protected void onPostExecute(AuthenticationProfile profile) {

        mListener.authProfileRefreshed(profile);
    }
}
