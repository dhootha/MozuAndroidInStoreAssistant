package com.mozu.mozuandroidinstoreassistant.app.views;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.tasks.ProductNameFetchListener;
import com.mozu.mozuandroidinstoreassistant.app.tasks.ProductNameFetchTask;

public class NetworkProductNameTextView extends TextView implements ProductNameFetchListener {

    private ProductNameFetchTask mTask;
    private Integer mTenantId;
    private Integer mSiteId;

    public NetworkProductNameTextView(Context context) {
        super(context);
    }

    public NetworkProductNameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkProductNameTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Integer tenantId, Integer siteId) {
        mTenantId = tenantId;
        mSiteId = siteId;

        mTask = new ProductNameFetchTask(this, mTenantId, mSiteId);
    }

    public void loadName(String locationCode) {
        if (mTenantId == null || mSiteId == null || mTask == null) {
            throw new IllegalStateException("you must init NetworkLocationNameTextView with tenant and site id before loading names");
        }

        mTask.cancel(true);

        mTask = new ProductNameFetchTask(this, mTenantId, mSiteId);

        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, locationCode);
    }

    @Override
    public void productNameLoaded(String name) {

        setText(name);
    }
}
