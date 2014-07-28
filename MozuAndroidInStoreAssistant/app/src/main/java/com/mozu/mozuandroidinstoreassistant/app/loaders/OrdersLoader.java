package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.resources.commerce.OrderResource;

import java.util.ArrayList;
import java.util.List;

public class OrdersLoader extends InternetConnectedAsyncTaskLoader<List<Order>> {

    private static final int ITEMS_PER_PAGE = 200;

    private List<Order> mOrdersList;
    private Integer mTenantId;
    private Integer mSiteId;

    private int mCurrentPage;
    private int mTotalPages;

    private boolean mIsLoading;

    public OrdersLoader(Context context, Integer tenantId, Integer siteId) {
        super(context);

        mTenantId = tenantId;
        mSiteId = siteId;

        init();
    }

    private void init() {
        cancelLoad();

        mCurrentPage = 0;
        mTotalPages = 0;

        mIsLoading = false;

        mOrdersList = new ArrayList<Order>();
    }

    @Override
    protected void onForceLoad() {

        super.onForceLoad();
    }

    @Override
    public List<Order> loadInBackground() {
        mIsLoading = true;

        super.loadInBackground();

        mOrdersList.addAll(loadOrdersFromWeb());

        mIsLoading = false;

        return mOrdersList;
    }

    @Override
    public void deliverResult(List<Order> data) {
        if (isReset())
            return;

        if (isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mOrdersList == null) {
            mOrdersList = new ArrayList<Order>();
        }

        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        mIsLoading = false;

        cancelLoad();
    }

    @Override
    public void onCanceled(List<Order> data) {
        mIsLoading = false;

        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        mOrdersList = null;

        super.onReset();
    }

    private List<Order> loadOrdersFromWeb() {
        List<Order> allOrders = new ArrayList<Order>();

        OrderCollection orderCollection;

        OrderResource orderResource = new OrderResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            orderCollection = orderResource.getOrders(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, null, null, null, null);

            mTotalPages = orderCollection.getTotalCount() / ITEMS_PER_PAGE;

            mCurrentPage += 1;

            allOrders = orderCollection.getItems();
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return allOrders;
    }

    public boolean hasMoreResults() {

        return mCurrentPage < mTotalPages;
    }

    public boolean isLoading() {

        return mIsLoading;
    }
}