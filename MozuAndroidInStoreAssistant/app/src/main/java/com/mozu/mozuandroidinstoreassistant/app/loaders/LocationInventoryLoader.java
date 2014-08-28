package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.DataViewMode;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productadmin.LocationInventoryCollection;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.resources.commerce.catalog.admin.products.LocationInventoryResource;

public class LocationInventoryLoader extends InternetConnectedAsyncTaskLoader<LocationInventoryCollection> {

    private Integer mTenantId;
    private Integer mSiteId;
    private Product mProduct;

    public LocationInventoryLoader(Context context, Integer tenantId, Integer siteId, Product product) {
        super(context);

        mTenantId = tenantId;
        mSiteId = siteId;
        mProduct = product;
    }

    @Override
    protected void onForceLoad() {

        super.onForceLoad();
    }

    @Override
    public LocationInventoryCollection loadInBackground() {
        super.loadInBackground();

        return loadLocationInventoryCollectionFromWeb();
    }

    @Override
    public void deliverResult(LocationInventoryCollection data) {
        if (isReset())
            return;

        if (isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {

        cancelLoad();
    }

    @Override
    public void onCanceled(LocationInventoryCollection data) {

        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();

        super.onReset();
    }

    private LocationInventoryCollection loadLocationInventoryCollectionFromWeb() {
        LocationInventoryCollection inventoryCollection = null;
        LocationInventoryResource inventoryResource = new LocationInventoryResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            inventoryCollection = inventoryResource.getLocationInventories(DataViewMode.Live, mProduct.getProductCode());
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

        return inventoryCollection;
    }
}