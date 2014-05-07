package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.mozu.api.security.AuthTicket;
import com.mozu.api.security.UserAuthenticator;

public class LogoutUserAsyncTask extends AsyncTask<Void, Void, Void> {

    private AuthTicket mAuthTicket;

    public LogoutUserAsyncTask(AuthTicket authTicket) {

        mAuthTicket = authTicket;

    }

    @Override
    protected Void doInBackground(Void... params) {

        UserAuthenticator.logout(mAuthTicket);

        return null;
    }
}
