package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.resources.commerce.catalog.storefront.ProductResource;

public class ProductDetailLoader extends AsyncTaskLoader<Product> {

    private Product mProduct;
    private Integer mTenantId;
    private Integer mSiteId;
    private String mProductCode;

    public ProductDetailLoader(Context context, Integer tenantId, Integer siteId, String productCode) {
        super(context);

        mTenantId = tenantId;
        mSiteId = siteId;
        mProductCode = productCode;
    }

    @Override
    protected void onForceLoad() {

        super.onForceLoad();
    }

    @Override
    public Product loadInBackground() {
        mProduct = loadProductFromWeb();

        return mProduct;
    }

    @Override
    public void deliverResult(Product data) {
        if (isReset())
            return;

        if (isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mProduct == null) {
            mProduct = new Product();
        }

        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {

        cancelLoad();
    }

    @Override
    public void onCanceled(Product data) {

        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        mProduct = null;

        super.onReset();
    }

    private Product loadProductFromWeb() {
        Product product = new Product();

        ProductResource productResource = new ProductResource(new MozuApiContext(mTenantId, mSiteId));

        try {

            product = productResource.getProduct(mProductCode);
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return product;
    }
}