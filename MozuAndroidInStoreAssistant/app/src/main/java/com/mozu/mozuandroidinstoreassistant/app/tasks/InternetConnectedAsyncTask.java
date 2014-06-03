package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.NetworkStateReporter;

public abstract class InternetConnectedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private Context mContext;

    public InternetConnectedAsyncTask(Context context) {

        mContext = context;

    }

    @Override
    protected Result doInBackground(Params... params) {

        try {
            if (NetworkStateReporter.isOnline(mContext)) {
                return null;
            } else {
                showNotConnectedToast();
            }
        } catch (Exception e) {
            showNotConnectedToast();
        }

        return null;
    }

    private void showNotConnectedToast() {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mContext.getString(R.string.connection_unavailable), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
