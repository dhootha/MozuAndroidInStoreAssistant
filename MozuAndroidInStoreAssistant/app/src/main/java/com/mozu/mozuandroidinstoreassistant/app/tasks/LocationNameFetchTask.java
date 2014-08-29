package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.location.Location;
import com.mozu.api.resources.commerce.LocationResource;

public class LocationNameFetchTask extends AsyncTask<String,Void,String> {

    private LocationNameFetchListener mListener;
    private Integer mTenantId;
    private Integer mSiteId;

    public LocationNameFetchTask(LocationNameFetchListener listener, Integer tenantId, Integer siteId){
        mListener = listener;
        mTenantId = tenantId;
        mSiteId = siteId;
    }

    @Override
    protected String doInBackground(String... strings) {
        LocationResource locationResource = new LocationResource(new MozuApiContext(mTenantId, mSiteId));

        try {
            Location location =  locationResource.getLocation(strings[0]);
            return location.getName();
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

             mListener.locationNameLoaded(locationName);
        }
    }

}
