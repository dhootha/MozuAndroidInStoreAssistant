package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.returns.Return;
import com.mozu.api.contracts.commerceruntime.returns.ReturnCollection;
import com.mozu.api.resources.commerce.ReturnResource;

import java.util.ArrayList;
import java.util.List;

public class ReturnLoader extends AsyncTaskLoader<List<Return>> {

    private List<Return> mReturns;
    private Integer mTenantId;
    private Integer mSiteId;
    private String mOrderNumber;

    public ReturnLoader(Context context, Integer tenantId, Integer siteId, String orderNumber) {
        super(context);

        mTenantId = tenantId;
        mSiteId = siteId;
        mOrderNumber = orderNumber;
    }

    @Override
    protected void onForceLoad() {

        super.onForceLoad();
    }

    @Override
    public List<Return> loadInBackground() {
        mReturns = loadOrderFromWeb();

        return mReturns;
    }

    @Override
    public void deliverResult(List<Return> data) {
        if (isReset())
            return;

        if (isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mReturns == null) {
            mReturns = new ArrayList<Return>();
        }

        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {

        cancelLoad();
    }

    @Override
    public void onCanceled(List<Return> data) {

        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        mReturns = null;

        super.onReset();
    }

    private List<Return> loadOrderFromWeb() {
        List<Return> returns = new ArrayList<Return>();

        ReturnResource orderResource = new ReturnResource(new MozuApiContext(mTenantId, mSiteId));

        ReturnCollection collection;

        try {
            collection = orderResource.getReturns(0, 200, null, "originalorderId eq " + mOrderNumber + " or returnorderid eq " + mOrderNumber, null);
            if (collection != null) {
                returns = collection.getItems();
            }
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return returns;
    }
}