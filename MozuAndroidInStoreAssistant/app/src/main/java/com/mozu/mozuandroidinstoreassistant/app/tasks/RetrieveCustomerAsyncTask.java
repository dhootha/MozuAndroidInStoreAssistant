package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;

public class RetrieveCustomerAsyncTask extends InternetConnectedAsyncTask<Void, Void, CustomerAccount> {

    private CustomerAsyncListener mListener;
    private Integer mTenantId;
    private Integer mSiteId;
    private Integer mCustomerId;

    public RetrieveCustomerAsyncTask(Context context, CustomerAsyncListener listener, Integer siteId, Integer tenantId, Integer customerId) {
        super(context);

        mListener = listener;

        mTenantId = tenantId;
        mSiteId = siteId;
        mCustomerId = customerId;
    }

    @Override
    protected CustomerAccount doInBackground(Void... params) {
        super.doInBackground(params);

        CustomerAccountResource customerAccountResource = new CustomerAccountResource(new MozuApiContext(mTenantId, mSiteId));

        CustomerAccount customerAccount = null;

        try {
            customerAccount = customerAccountResource.getAccount(mCustomerId);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        return customerAccount;
    }

    @Override
    protected void onPostExecute(CustomerAccount customer) {
        super.onPostExecute(customer);

        mListener.customerRetrieved(customer);
    }
}
