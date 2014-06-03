package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.tenant.Tenant;
import com.mozu.api.resources.platform.TenantResource;
import com.mozu.api.security.Scope;
import com.mozu.mozuandroidinstoreassistant.app.TenantResourceAsyncListener;

public class RetrieveTenantAsyncTask extends InternetConnectedAsyncTask<Void, Void, Tenant> {

    private TenantResourceAsyncListener mListener;
    private Scope mCurrentScope;

    public RetrieveTenantAsyncTask(Context context, Scope scope, TenantResourceAsyncListener listener) {
        super(context);

        mCurrentScope = scope;
        mListener = listener;
    }

    @Override
    protected Tenant doInBackground(Void... params) {
        super.doInBackground(params);

        TenantResource tenantResource = new TenantResource(new MozuApiContext());

        try {
            return tenantResource.getTenant(mCurrentScope.getId());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Tenant tenant) {
        super.onPostExecute(tenant);

        mListener.retrievedTenant(tenant);
    }
}
