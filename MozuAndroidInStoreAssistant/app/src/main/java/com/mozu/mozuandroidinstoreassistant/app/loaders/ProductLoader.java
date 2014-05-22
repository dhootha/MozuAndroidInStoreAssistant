package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productruntime.ProductCollection;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.resources.commerce.catalog.storefront.ProductResource;

import java.util.ArrayList;
import java.util.List;

public class ProductLoader extends AsyncTaskLoader<List<Product>> {

    private List<Product> mProductList;
    private Integer mTenantId;
    private Integer mSiteId;
    private Integer mCategoryId;

    public ProductLoader(Context context, Integer tenantId, Integer siteId, Integer categoryId) {
        super(context);

        mTenantId = tenantId;
        mSiteId = siteId;
        mCategoryId = categoryId;
    }

    @Override
    protected void onForceLoad() {

        super.onForceLoad();
    }

    @Override
    public List<Product> loadInBackground() {
        mProductList = loadProductFromWeb();

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

        cancelLoad();
    }

    @Override
    public void onCanceled(List<Product> data) {

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
            productCollection = productResource.getProducts("categoryId eq " + String.valueOf(mCategoryId), 0, 200, "productname asc");

            allProducts = productCollection.getItems();
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return allProducts;
    }

}