package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.tenant.Tenant;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.api.resources.platform.TenantResource;
import com.mozu.api.security.Scope;
import com.mozu.mozuandroidinstoreassistant.app.TenantResourceAsyncListener;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

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

        mListener.customerRetreived(customer);
    }
}
