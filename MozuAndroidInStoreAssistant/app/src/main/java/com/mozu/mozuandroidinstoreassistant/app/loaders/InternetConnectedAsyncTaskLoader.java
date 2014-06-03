package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.NetworkStateReporter;

public abstract class InternetConnectedAsyncTaskLoader<D> extends AsyncTaskLoader<D> {

    public InternetConnectedAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public D loadInBackground() {
        try {
            if (NetworkStateReporter.isOnline(getContext())) {
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
                Toast.makeText(getContext(), getContext().getString(R.string.connection_unavailable), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
