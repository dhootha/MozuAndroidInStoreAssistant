package com.mozu.mozuandroidinstoreassistant.app.order.loaders;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.mozuandroidinstoreassistant.app.loaders.InternetConnectedAsyncTaskLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OrdersLoader extends InternetConnectedAsyncTaskLoader<List<Order>> {

    public static final String FILTER_BY_STATUS = "status eq Accepted";
    private static final int ITEMS_PER_PAGE = 50;
    private static final String ORDER_ID_FILTER_BY = "orderNumber eq ";
    private static final String FILTER_ABANDONED = "status ne Abandoned";
    private static final String FILTER_PENDING = "status ne Pending";
    private static final String ORDER_ORDER_NUMBER = "orderNumber";

    private static final String ORDER_ORDER_DATE = "submittedDate";
    private static final String ORDER_ORDER_EMAIL = "email";
    private static final String ORDER_ORDER_STATUS = "status";
    private static final String ORDER_ORDER_TOTAL = "total";
    private static final String ORDER_ORDER_PAYMENT_STATUS = "paymentStatus";

    private static final String SORT_ORDER_ASC = "asc";
    private static final String SORT_ORDER_DSC = "desc";
    public String mCurrentOrderBy = "";
    public String mSearchQueryFilter;
    private String RESPONSE_FIELDS = "items(id,ordernumber,status,SubmittedDate,paymentStatus,total,status)";
    private List<Order> mOrdersList;
    private Integer mTenantId;
    private Integer mSiteId;
    private int mCurrentPage;
    private int mTotalPages;
    private boolean mIsLoading;
    private String mCurrentSort;
    private String mFilter = FILTER_ABANDONED + " and " +FILTER_PENDING;

    public OrdersLoader(Context context, Integer tenantId, Integer siteId) {
        super(context);
        mTenantId = tenantId;
        mSiteId = siteId;
        mCurrentSort = SORT_ORDER_DSC;
        mCurrentOrderBy = ORDER_ORDER_DATE;
        init();
    }

    public boolean isSortAsc() {
        return SORT_ORDER_ASC.equals(mCurrentSort);
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
            if (!TextUtils.isEmpty(mSearchQueryFilter)) {
                orderCollection = searchOrders(orderResource);
            } else {
                orderCollection = orderResource.getOrders(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, mCurrentOrderBy + " " + mCurrentSort, mFilter, null, null, RESPONSE_FIELDS);
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
            orderCollection = orderResource.getOrders(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, null, ORDER_ID_FILTER_BY + mSearchQueryFilter + " and " + FILTER_ABANDONED, null, null, RESPONSE_FIELDS);
        } else {
            orderCollection = orderResource.getOrders(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, null, FILTER_ABANDONED, mSearchQueryFilter, null, RESPONSE_FIELDS);
        }

        return orderCollection;
    }

    public boolean hasMoreResults() {

        return mCurrentPage < mTotalPages;
    }

    public boolean isLoading() {

        return mIsLoading;
    }

    public void setQueryFilter(String query) {
        mSearchQueryFilter = query;
    }

    public void setFilter(String filter) {
        if(filter != null && filter.contains("status eq Pending")) {
            mFilter = FILTER_ABANDONED;
        }
        if (filter != null && !filter.isEmpty()) {
            mFilter = mFilter + " and " + filter;
        }
    }

    public void removeQuery() {
        mSearchQueryFilter = "";
    }

    public void removeFilter() {
        mFilter = FILTER_ABANDONED + " and " + FILTER_PENDING;
    }

    private void toggleCurrentSortOrder() {
        if (SORT_ORDER_DSC.equalsIgnoreCase(mCurrentSort)) {
            mCurrentSort = SORT_ORDER_ASC;
        } else {
            mCurrentSort = SORT_ORDER_DSC;
        }
    }

    public void orderByNumber() {
        mCurrentPage = 0;
        if (ORDER_ORDER_NUMBER.equalsIgnoreCase(mCurrentOrderBy)) {
            toggleCurrentSortOrder();
        } else {
            mCurrentOrderBy = ORDER_ORDER_NUMBER;
            mCurrentSort = SORT_ORDER_DSC;
        }
    }

    //date is the default and there is no way through the api/sdk to
    //tell the orders to sort by a date
    //so we are just removing the sort filter and resorting
    public void orderByDate() {
        mCurrentPage = 0;
        if (ORDER_ORDER_DATE.equalsIgnoreCase(mCurrentOrderBy)) {
            toggleCurrentSortOrder();
        } else {
            mCurrentOrderBy = ORDER_ORDER_DATE;
            mCurrentSort = SORT_ORDER_DSC;
        }
    }

    public void orderByStatus() {
        mCurrentPage = 0;
        if (ORDER_ORDER_STATUS.equalsIgnoreCase(mCurrentOrderBy)) {
            toggleCurrentSortOrder();
        } else {
            mCurrentOrderBy = ORDER_ORDER_STATUS;
            mCurrentSort = SORT_ORDER_DSC;
        }

    }

    public void orderByPaymentStatus() {
        mCurrentPage = 0;
        if (ORDER_ORDER_PAYMENT_STATUS.equalsIgnoreCase(mCurrentOrderBy)) {
            toggleCurrentSortOrder();
        } else {
            mCurrentOrderBy = ORDER_ORDER_PAYMENT_STATUS;
            mCurrentSort = SORT_ORDER_DSC;
        }
    }

    public void orderByTotal() {
        mCurrentPage = 0;
        if (ORDER_ORDER_TOTAL.equalsIgnoreCase(mCurrentOrderBy)) {
            toggleCurrentSortOrder();
        } else {
            mCurrentOrderBy = ORDER_ORDER_TOTAL;
            mCurrentSort = SORT_ORDER_DSC;
        }
    }

    public void clearOrdering() {

        mCurrentOrderBy = "";
    }
}