package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.mozuandroidinstoreassistant.app.models.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OrdersLoader extends InternetConnectedAsyncTaskLoader<List<Order>> {

    private static final int ITEMS_PER_PAGE = 200;

    private static final String ORDER_ID_FILTER_BY = "orderNumber eq ";

    private static final String ORDER_ORDER_NUMBER = "orderNumber";
    private static final String ORDER_ORDER_DATE = "orderDate";
    private static final String ORDER_ORDER_EMAIL = "email";
    private static final String ORDER_ORDER_STATUS = "status";
    private static final String ORDER_ORDER_TOAL = "total";

    public String mCurrentOrderBy = "";

    private List<Order> mOrdersList;
    private Integer mTenantId;
    private Integer mSiteId;

    private int mCurrentPage;
    private int mTotalPages;

    private boolean mIsLoading;

    public String mSearchQueryFilter;

    public OrdersLoader(Context context, Integer tenantId, Integer siteId) {
        super(context);
        mTenantId = tenantId;
        mSiteId = siteId;
        init();
    }

    public void init() {
        cancelLoad();

        mCurrentPage = 0;
        mTotalPages = 0;

        mIsLoading = false;

        mSearchQueryFilter = "";

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

        List<Order> tmpOrder = new ArrayList<Order>();
        tmpOrder.addAll(mOrdersList);

        mOrdersList = null;

        mOrdersList = new ArrayList<Order>(tmpOrder);

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

        init();

        super.onReset();
    }

    private List<Order> loadOrdersFromWeb() {
        List<Order> allOrders = new ArrayList<Order>();

        OrderCollection orderCollection;

        OrderResource orderResource = new OrderResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            if (mSearchQueryFilter != null && !mSearchQueryFilter.equalsIgnoreCase("")) {
                orderCollection = searchOrders(orderResource);
            } else {
                orderCollection = orderResource.getOrders(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, mCurrentOrderBy, null, null, null);
            }

            mTotalPages = (int) Math.ceil(orderCollection.getTotalCount() * 1.0f / ITEMS_PER_PAGE * 1.0f);

            mCurrentPage += 1;

            allOrders = orderCollection.getItems();
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return allOrders;
    }

    private OrderCollection searchOrders(OrderResource orderResource) throws Exception {
        OrderCollection orderCollection;

        if (StringUtils.isNumber(mSearchQueryFilter)) {
            orderCollection = orderResource.getOrders(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, null, ORDER_ID_FILTER_BY + mSearchQueryFilter, null, null);
        } else {
            orderCollection = orderResource.getOrders(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, null, null, mSearchQueryFilter, null);
        }
        return orderCollection;
    }

    public boolean hasMoreResults() {

        return mCurrentPage < mTotalPages;
    }

    public boolean isLoading() {

        return mIsLoading;
    }

    public void setFilter(String filter) {
        mSearchQueryFilter = filter;
    }

    public void removeFilter() {
        mSearchQueryFilter = "";
    }

    public void orderByNumber() {

        mCurrentOrderBy = ORDER_ORDER_NUMBER;
    }

    //date is the default and there is no way through the api/sdk to
    //tell the orders to sort by a date
    //so we are just removing the sort filter and resorting
    public void orderByDate() {

        clearOrdering();
    }

//TODO: NOT CURRENTLY A WAY TO SORT BY EMAIL
//    public void orderByEmail() {
//
//        mCurrentOrderBy = ORDER_ORDER_EMAIL;
//    }

    public void orderByStatus() {

        mCurrentOrderBy = ORDER_ORDER_STATUS;
    }

    public void orderByTotal() {

        mCurrentOrderBy = ORDER_ORDER_TOAL;
    }

    public void clearOrdering() {

        mCurrentOrderBy = "";
    }
}