package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.api.resources.commerce.catalog.storefront.ProductSearchResultResource;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchLoader extends InternetConnectedAsyncTaskLoader<List<Product>> {

    private static final int ITEMS_PER_PAGE = 200;
    public static final String SORT_BY = "productname asc";
    public static final String CATEGORY_FILTER = "categoryId eq ";

    private List<Product> mProductList;
    private Integer mTenantId;
    private Integer mSiteId;
    private Integer mCategoryId;

    private int mCurrentPage;
    private int mTotalPages;

    private boolean mIsLoading;

    private String mQueryString;

    public ProductSearchLoader(Context context, Integer tenantId, Integer siteId, Integer categoryId, String queryString) {
        super(context);

        mTenantId = tenantId;
        mSiteId = siteId;
        mCategoryId = categoryId;
        mQueryString = queryString;

        init();
    }

    private void init() {
        cancelLoad();

        mCurrentPage = 0;
        mTotalPages = 0;

        mIsLoading = false;

        mProductList = new ArrayList<Product>();
    }

    @Override
    protected void onForceLoad() {

        super.onForceLoad();
    }

    @Override
    public List<Product> loadInBackground() {
        mIsLoading = true;

        super.loadInBackground();

        if (mProductList == null) {
            init();
        }

        mProductList.addAll(loadProductFromWeb());

        mIsLoading = false;

        return mProductList;
    }

    @Override
    public void deliverResult(List<Product> data) {
        if (isReset())
            return;

        if (isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mProductList == null) {
            mProductList = new ArrayList<Product>();
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
    public void onCanceled(List<Product> data) {
        mIsLoading = false;

        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        mProductList = null;

        super.onReset();
    }

    private List<Product> loadProductFromWeb() {
        List<Product> allProducts = new ArrayList<Product>();

        ProductSearchResultResource searchResultResource = new ProductSearchResultResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            ProductSearchResult result = searchResultResource.search(mQueryString, mCategoryId == -1 || mCategoryId == 0 || mCategoryId == null ? null : CATEGORY_FILTER + String.valueOf(mCategoryId),
                    null, null, null, null, null, null, null, null, null, null, null,
                    SORT_BY, ITEMS_PER_PAGE, mCurrentPage * ITEMS_PER_PAGE, null);

            mTotalPages = result.getTotalCount() / ITEMS_PER_PAGE;

            mCurrentPage += 1;

            allProducts = result.getItems();
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return allProducts;
    }

    public boolean hasMoreResults() {

        return mCurrentPage < mTotalPages;
    }

    public boolean isLoading() {

        return mIsLoading;
    }

    public void setSearchQuery(String query) {
        mQueryString = query;
    }
}