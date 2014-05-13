package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.security.AuthenticationProfile;
import com.mozu.mozuandroidinstoreassistant.app.serialization.CurrentAuthTicketSerializer;

import java.io.IOException;

public class WriteAuthProfileToDiskAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private AuthenticationProfile mProfile;

    public WriteAuthProfileToDiskAsyncTask(AuthenticationProfile profile, Context context) {

        mProfile = profile;
        mContext = context;

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        CurrentAuthTicketSerializer authTicketSerializer = new CurrentAuthTicketSerializer(mContext);

        try {
            authTicketSerializer.serializeAuthProfileSynchronously(mProfile);

            return true;
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

        return false;
    }
}
