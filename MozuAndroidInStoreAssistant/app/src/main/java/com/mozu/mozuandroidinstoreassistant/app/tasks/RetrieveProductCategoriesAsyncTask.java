package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.mozu.api.contracts.productadmin.Category;

import java.util.List;

/**
 * Created by matt_wear on 5/19/14.
 */
public class RetrieveProductCategoriesAsyncTask extends AsyncTask<Void, Void, List<Category>> {

    private String mTenantId;
    private String mSiteId;

    public RetrieveProductCategoriesAsyncTask(String tenantId, String siteId) {
        mTenantId = tenantId;
        mSiteId = siteId;
    }

    @Override
    protected List<Category> doInBackground(Void... params) {


        return null;
    }

}
