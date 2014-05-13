package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserPreferencesDiskInteractorListener;
import com.mozu.mozuandroidinstoreassistant.app.serialization.UserPreferencesSerializer;

import java.io.IOException;
import java.util.List;

public class WriteUserPrefsFromDiskAsyncTask extends AsyncTask<Void, Void, Void> {

    private UserPreferencesDiskInteractorListener mListener;
    private Context mContext;
    private List<UserPreferences> mPreferences;
    private boolean errored = false;

    public WriteUserPrefsFromDiskAsyncTask(UserPreferencesDiskInteractorListener listener, Context context, List<UserPreferences> preferences) {
        mListener = listener;
        mContext = context;
        mPreferences = preferences;
    }

    @Override
    protected Void doInBackground(Void... params) {

        UserPreferencesSerializer serializer = new UserPreferencesSerializer(mContext);

        try {
            serializer.serializeUserPrefs(mPreferences);
        } catch (IOException e) {
            Crashlytics.logException(e);
            errored = true;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (errored) {
            mListener.failedToWrite();
        }

        super.onPostExecute(aVoid);
    }

    @Override
    protected void onCancelled() {
        if (errored) {
            mListener.failedToWrite();
        }

        super.onCancelled();
    }
}
