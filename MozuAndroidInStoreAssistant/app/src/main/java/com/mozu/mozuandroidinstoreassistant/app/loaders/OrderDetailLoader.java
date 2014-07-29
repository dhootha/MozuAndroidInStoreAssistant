package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.resources.commerce.OrderResource;

public class OrderDetailLoader extends AsyncTaskLoader<Order> {

    private Order mOrder;
    private Integer mTenantId;
    private Integer mSiteId;
    private String mOrderNumber;

    public OrderDetailLoader(Context context, Integer tenantId, Integer siteId, String orderNumber) {
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
    public Order loadInBackground() {
        mOrder = loadOrderFromWeb();

        return mOrder;
    }

    @Override
    public void deliverResult(Order data) {
        if (isReset())
            return;

        if (isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mOrder == null) {
            mOrder = new Order();
        }

        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {

        cancelLoad();
    }

    @Override
    public void onCanceled(Order data) {

        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        mOrder = null;

        super.onReset();
    }

    private Order loadOrderFromWeb() {
        Order order = new Order();

        OrderResource orderResource = new OrderResource(new MozuApiContext(mTenantId, mSiteId));

        try {

            order = orderResource.getOrder(mOrderNumber);
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return order;
    }
}