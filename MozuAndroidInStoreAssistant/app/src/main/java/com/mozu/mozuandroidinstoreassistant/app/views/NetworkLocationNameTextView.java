package com.mozu.mozuandroidinstoreassistant.app.views;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.tasks.LocationNameFetchListener;
import com.mozu.mozuandroidinstoreassistant.app.tasks.LocationNameFetchTask;

public class NetworkLocationNameTextView extends TextView implements LocationNameFetchListener {

    private LocationNameFetchTask mTask;
    private Integer mTenantId;
    private Integer mSiteId;

    public NetworkLocationNameTextView(Context context) {
        super(context);
    }

    public NetworkLocationNameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkLocationNameTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Integer tenantId, Integer siteId) {
        mTenantId = tenantId;
        mSiteId = siteId;

        mTask = new LocationNameFetchTask(this, mTenantId, mSiteId);
    }

    public void loadName(String locationCode) {
        if (mTenantId == null || mSiteId == null || mTask == null) {
            throw new IllegalStateException("you must init NetworkLocationNameTextView with tenant and site id before loading names");
        }

        mTask.cancel(true);

        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void locationNameLoaded(String name) {
        setText(name);
    }
}
