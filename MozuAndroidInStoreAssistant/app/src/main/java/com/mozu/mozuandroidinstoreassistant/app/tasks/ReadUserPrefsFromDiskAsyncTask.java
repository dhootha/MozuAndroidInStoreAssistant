package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserPreferencesDiskInteractorListener;
import com.mozu.mozuandroidinstoreassistant.app.serialization.UserPreferencesSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadUserPrefsFromDiskAsyncTask extends AsyncTask<Void, Void, List<UserPreferences>> {

    private UserPreferencesDiskInteractorListener mListener;
    private Context mContext;

    public ReadUserPrefsFromDiskAsyncTask(UserPreferencesDiskInteractorListener listener, Context context) {
        mListener = listener;
        mContext = context;
    }

    @Override
    protected List<UserPreferences> doInBackground(Void... params) {

        UserPreferencesSerializer serializer = new UserPreferencesSerializer(mContext);

        List<UserPreferences> prefs = null;

        try {
            prefs = serializer.deseraializeUserPrefs();
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

        return prefs;
    }

    @Override
    protected void onPostExecute(List<UserPreferences> userPreferences) {
        super.onPostExecute(userPreferences);

        if (userPreferences == null) {
            userPreferences = new ArrayList<UserPreferences>();
        }

        mListener.finishedReading(userPreferences);
    }
}
