package com.mozu.mozuandroidinstoreassistant.app.order.loaders;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.mozuandroidinstoreassistant.app.loaders.InternetConnectedAsyncTaskLoader;

public class OrderCreateLoader extends InternetConnectedAsyncTaskLoader<Order>{

    private Integer  mTenantId;
    private Integer mSiteId;

    public OrderCreateLoader(Context context, Integer tenantId, Integer siteId) {
        super(context);
        mTenantId = tenantId;
        mSiteId = siteId;
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    @Override
    public Order loadInBackground() {
        OrderResource orderResource = new OrderResource(new MozuApiContext(mTenantId, mSiteId));
        try {
            return orderResource.createOrder(new Order());
        } catch (Exception e) {
            Log.e(OrderCreateLoader.class.getSimpleName(), e.toString());
            Crashlytics.logException(e);
        }
        return super.loadInBackground();
    }



}
