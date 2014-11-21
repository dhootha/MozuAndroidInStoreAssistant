package com.mozu.mozuandroidinstoreassistant.app.customer.loaders;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerAccountCollection;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.mozuandroidinstoreassistant.app.loaders.InternetConnectedAsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

public class CustomersLoader extends InternetConnectedAsyncTaskLoader<List<CustomerAccount>> {

    private static final int ITEMS_PER_PAGE = 200;

    private static final String CUSTOMER_CUSTOMER_NUMBER = "id";
    private static final String CUSTOMER_CUSTOMER_LIFETIME_VALUE = "commercesummary.totalorderamount";
    private static final String SORT_CUSTOMER_ASC = "asc";
    private static final String SORT_CUSTOMER_DSC = "desc";

    private String mCurrentOrderBy = "";

    private List<CustomerAccount> mCustomersList;
    private Integer mTenantId;
    private Integer mSiteId;

    private int mCurrentPage;
    private int mTotalPages;

    private boolean mIsLoading;

    private String mSearchQueryFilter;
    private String mCurrentSort;

    public CustomersLoader(Context context, Integer tenantId, Integer siteId) {
        super(context);
        mTenantId = tenantId;
        mSiteId = siteId;
        mCurrentSort = SORT_CUSTOMER_DSC;
        mCurrentOrderBy = CUSTOMER_CUSTOMER_NUMBER;
        init();
    }

    public boolean isSortAsc() {
        return SORT_CUSTOMER_ASC.equals(mCurrentSort);
    }

    protected void init() {
        cancelLoad();

        mCurrentPage = 0;
        mTotalPages = 0;

        mIsLoading = false;

        mSearchQueryFilter = "";

        mCustomersList = new ArrayList<CustomerAccount>();

    }

    @Override
    protected void onForceLoad() {

        super.onForceLoad();
    }

    @Override
    public List<CustomerAccount> loadInBackground() {
        mIsLoading = true;

        super.loadInBackground();

        mCustomersList.addAll(loadCustomersFromWeb());

        List<CustomerAccount> tmpCustomer = new ArrayList<CustomerAccount>();
        tmpCustomer.addAll(mCustomersList);

        mCustomersList = null;

        mCustomersList = new ArrayList<CustomerAccount>(tmpCustomer);

        mIsLoading = false;

        return mCustomersList;
    }

    @Override
    public void deliverResult(List<CustomerAccount> data) {
        if (isReset())
            return;

        if (isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mCustomersList == null) {
            mCustomersList = new ArrayList<CustomerAccount>();
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
    public void onCanceled(List<CustomerAccount> data) {
        mIsLoading = false;

        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        mCustomersList = null;

        init();

        super.onReset();
    }

    private List<CustomerAccount> loadCustomersFromWeb() {
        List<CustomerAccount> allCustomers = new ArrayList<CustomerAccount>();

        CustomerAccountCollection customerCollection;

        CustomerAccountResource customerResource = new CustomerAccountResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            if (!TextUtils.isEmpty(mSearchQueryFilter)) {
                customerCollection = customerResource.getAccounts(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, null, null, null, mSearchQueryFilter, null, false, null);
            } else {
                customerCollection = customerResource.getAccounts(mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, mCurrentOrderBy +" "+mCurrentSort, null, null, null, null, false, null);
            }

            mTotalPages = (int) Math.ceil(customerCollection.getTotalCount() * 1.0f / ITEMS_PER_PAGE * 1.0f);

            mCurrentPage += 1;

            allCustomers = customerCollection.getItems();
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return allCustomers;
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

    private void toggleCurrentSortCustomer(){
        if (SORT_CUSTOMER_DSC.equalsIgnoreCase(mCurrentSort)) {
            mCurrentSort = SORT_CUSTOMER_ASC;
        } else {
            mCurrentSort =  SORT_CUSTOMER_DSC;
        }
    }

    public void customerByNumber() {
        mCurrentPage = 0;
        if (CUSTOMER_CUSTOMER_NUMBER.equalsIgnoreCase(mCurrentOrderBy)) {
            toggleCurrentSortCustomer();
        } else {
            mCurrentOrderBy = CUSTOMER_CUSTOMER_NUMBER;
            mCurrentSort = SORT_CUSTOMER_DSC;
        }
    }

//TODO: Unsupported Sort Operations
//    public void customerByLastName() {
//        mCurrentPage = 0;
//        if (CUSTOMER_LAST_NAME.equalsIgnoreCase(mCurrentOrderBy)) {
//            toggleCurrentSortCustomer();
//        } else {
//            mCurrentOrderBy = CUSTOMER_LAST_NAME;
//            mCurrentSort = SORT_CUSTOMER_DSC;
//        }
//    }
//
//    public void customerByFirstName() {
//        mCurrentPage = 0;
//        if (CUSTOMER_FIRST_NAME.equalsIgnoreCase(mCurrentOrderBy)) {
//            toggleCurrentSortCustomer();
//        } else {
//            mCurrentOrderBy = CUSTOMER_FIRST_NAME;
//            mCurrentSort = SORT_CUSTOMER_DSC;
//        }
//
//    }
//
//    public void customerByEmail() {
//        mCurrentPage = 0;
//        if (CUSTOMER_CUSTOMER_EMAIL.equalsIgnoreCase(mCurrentOrderBy)) {
//            toggleCurrentSortCustomer();
//        } else {
//            mCurrentOrderBy = CUSTOMER_CUSTOMER_EMAIL;
//            mCurrentSort = SORT_CUSTOMER_DSC;
//        }
//    }

    public void customerByTotal() {
        mCurrentPage = 0;
        if (CUSTOMER_CUSTOMER_LIFETIME_VALUE.equalsIgnoreCase(mCurrentOrderBy)) {
            toggleCurrentSortCustomer();
        } else {
            mCurrentOrderBy = CUSTOMER_CUSTOMER_LIFETIME_VALUE;
            mCurrentSort = SORT_CUSTOMER_DSC;
        }
    }

    public void clearOrdering() {

        mCurrentOrderBy = "";
    }
}