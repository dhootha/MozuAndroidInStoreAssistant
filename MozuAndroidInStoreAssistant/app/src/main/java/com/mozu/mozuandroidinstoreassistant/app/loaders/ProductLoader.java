package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.productruntime.ProductCollection;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.resources.commerce.catalog.storefront.ProductResource;

import java.util.ArrayList;
import java.util.List;

public class ProductLoader extends InternetConnectedAsyncTaskLoader<List<Product>> {

    private static final int ITEMS_PER_PAGE = 200;
    public static final String SORT_BY = "productname asc";
    public static final String FILTER_BY = "categoryId eq ";

    private List<Product> mProductList;
    private Integer mTenantId;
    private Integer mSiteId;
    private Integer mCategoryId;

    private int mCurrentPage;
    private int mTotalPages;

    private boolean mIsLoading;

    public ProductLoader(Context context, Integer tenantId, Integer siteId, Integer categoryId) {
        super(context);

        mTenantId = tenantId;
        mSiteId = siteId;
        mCategoryId = categoryId;

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

        mProductList.addAll(loadProductFromWeb());

        List<Product> tmpProduct = new ArrayList<Product>();
        tmpProduct.addAll(mProductList);

        mProductList = null;

        mProductList = new ArrayList<Product>(tmpProduct);

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

        ProductCollection productCollection;

        ProductResource productResource = new ProductResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            productCollection = productResource.getProducts(FILTER_BY + String.valueOf(mCategoryId), mCurrentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE, SORT_BY);

            mTotalPages = (int) Math.ceil(productCollection.getTotalCount() * 1.0f / ITEMS_PER_PAGE * 1.0f);

            mCurrentPage += 1;

            allProducts = productCollection.getItems();
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
}