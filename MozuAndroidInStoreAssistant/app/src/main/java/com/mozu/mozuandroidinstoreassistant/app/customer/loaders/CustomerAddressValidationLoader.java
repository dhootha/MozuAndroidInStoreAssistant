package com.mozu.mozuandroidinstoreassistant.app.customer.loaders;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.customer.AddressValidationRequest;
import com.mozu.api.contracts.customer.AddressValidationResponse;
import com.mozu.api.resources.commerce.customer.AddressValidationRequestResource;
import com.mozu.mozuandroidinstoreassistant.app.loaders.InternetConnectedAsyncTaskLoader;

/**
 * Created by chris_pound on 8/7/15.
 */
public class CustomerAddressValidationLoader extends InternetConnectedAsyncTaskLoader<AddressValidationResponse> {

    private Address mAddress;
    private Integer mTenantApi;
    private Integer mSiteId;

    public CustomerAddressValidationLoader(Context context, Integer tenantApi, Integer siteId) {
        super(context);
        this.mTenantApi = tenantApi;
        this.mSiteId = siteId;
    }

    public void setAddressToVerify(Address address) {
        this.mAddress = address;
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    @Override
    protected AddressValidationResponse onLoadInBackground() {
        return loadInBackground();
    }

    @Override
    public AddressValidationResponse loadInBackground() {
        super.loadInBackground();
        if(mAddress == null) {
            Log.e("Address Validation", "Address must not be null");
            return null;
        }
        AddressValidationRequestResource addressValidationRequestResource = new AddressValidationRequestResource(new MozuApiContext(mTenantApi, mSiteId));
        AddressValidationRequest addressValidationRequest = new AddressValidationRequest();
        addressValidationRequest.setAddress(mAddress);
        try {
            return addressValidationRequestResource.validateAddress(addressValidationRequest);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.e("Address Loader", e.toString());
            return null;
        }
    }
}
