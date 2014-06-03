package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.ApiException;
import com.mozu.api.contracts.appdev.AppAuthInfo;
import com.mozu.api.security.AppAuthenticator;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.AppAuthenticatorAsyncListener;

public class AppAuthenticateAsyncTask extends InternetConnectedAsyncTask<Void, Void, Boolean> {

    private AppAuthenticatorAsyncListener mListener;
    private AppAuthInfo mAppAuthInfo;
    private String mBaseUrl;

    public AppAuthenticateAsyncTask(Context context, AppAuthInfo appAuthInfo, String baseUrl, AppAuthenticatorAsyncListener listener) {
        super(context);

        mAppAuthInfo = appAuthInfo;
        mBaseUrl = baseUrl;
        mListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        super.doInBackground(params);

        try {
            AppAuthenticator.initialize(mAppAuthInfo, mBaseUrl);
        } catch (ApiException e) {

            Crashlytics.logException(e);
            return false;
        } catch (Exception e) {

            Crashlytics.logException(e);
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        if (success) {
            mListener.appInitialized();
        } else {
            mListener.errorInitializingApp("");
        }
    }

}
