package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticatorAsyncListener;
import com.mozu.mozuandroidinstoreassistant.app.serialization.CurrentAuthTicketSerializer;

import java.io.IOException;

public class ObtainAuthProfileFromDiskAsyncTask extends AsyncTask<Void, Void, AuthenticationProfile> {

    private UserAuthenticatorAsyncListener mListener;
    private Context mContext;
    private boolean errored;

    public ObtainAuthProfileFromDiskAsyncTask(UserAuthenticatorAsyncListener listener, Context context) {

        mListener = listener;
        mContext = context;
    }

    @Override
    protected AuthenticationProfile doInBackground(Void... params) {
        errored = false;

        CurrentAuthTicketSerializer authTicketSerializer = new CurrentAuthTicketSerializer(mContext);

        try {
            return authTicketSerializer.deserializeAuthProfileSynchronously();
        } catch (IOException e) {
            Crashlytics.logException(e);
            //I prefer not to tell listener about the error on the background thread
            //so I've set an error state and will tell them in the post execute
            errored = true;
        }

        return null;
    }

    @Override
    protected void onPostExecute(AuthenticationProfile profile) {

        mListener.authProfileReadFromDisk(profile);

        if (errored) {
            mListener.errored("could not read auth profile from disk");
        }
    }

    @Override
    protected void onCancelled(AuthenticationProfile profile) {

        mListener.errored("could not read auth profile from disk");
    }
}
