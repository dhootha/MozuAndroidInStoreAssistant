package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.location.Location;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.resources.commerce.LocationResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductResource;

public class ProductNameFetchTask extends AsyncTask<String,Void,String> {

    private ProductNameFetchListener mListener;
    private Integer mTenantId;
    private Integer mSiteId;

    public ProductNameFetchTask(ProductNameFetchListener listener, Integer tenantId, Integer siteId){
        mListener = listener;
        mTenantId = tenantId;
        mSiteId = siteId;
    }

    @Override
    protected String doInBackground(String... strings) {
        ProductResource productResource = new ProductResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            Product product =  productResource.getProduct(strings[0]);

            if (product.getContent() == null) {
                return null;
            }

            return product.getContent().getProductName();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String locationName) {
        if (isCancelled()) {

            return;
        }

        if (mListener != null) {

             mListener.productNameLoaded(locationName);
        }
    }

}
