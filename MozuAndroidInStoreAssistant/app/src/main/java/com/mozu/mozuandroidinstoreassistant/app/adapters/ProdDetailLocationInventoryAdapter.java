package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.location.Location;
import com.mozu.api.contracts.productadmin.LocationInventory;
import com.mozu.api.resources.commerce.LocationResource;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class ProdDetailLocationInventoryAdapter extends ArrayAdapter<LocationInventory> {

    private Integer mTenantId;
    private Integer mSiteId;
    public ProdDetailLocationInventoryAdapter(Context context, List<LocationInventory> locationInventories,Integer tenantId,Integer siteID) {
        super(context, R.layout.inventory_list_item);
        mTenantId = tenantId;
        mSiteId = siteID;
        addAll(locationInventories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_list_item, parent, false);
        }

        LocationInventory locationInventory = getItem(position);

        TextView code = (TextView) convertView.findViewById(R.id.inventory_code);
        TextView name = (TextView) convertView.findViewById(R.id.inventory_name);
        TextView available = (TextView) convertView.findViewById(R.id.inventory_available);
        TextView onHand = (TextView) convertView.findViewById(R.id.inventory_on_hand);
        TextView onReserve = (TextView) convertView.findViewById(R.id.inventory_on_reserve);

        code.setText(locationInventory.getLocationCode());
        available.setText( String.valueOf(locationInventory.getStockAvailable()));
        onHand.setText(locationInventory.getStockOnHand().toString());
        onReserve.setText(locationInventory.getStockOnBackOrder().toString());
        if(name.getTag() instanceof LocationNameFetchTask ) {
           ((LocationNameFetchTask) name.getTag()).cancel(true);
        }
        LocationNameFetchTask task = new LocationNameFetchTask(name);
        name.setTag(task);
        task.execute(locationInventory.getLocationCode());
        return convertView;
    }

    private class LocationNameFetchTask extends AsyncTask<String,Void,String>{
        private  final WeakReference<TextView> mTextViewReference;
        private LocationNameFetchTask(TextView textView){
            mTextViewReference = new WeakReference<TextView>(textView);
        }
        @Override
        protected String doInBackground(String... strings) {
            LocationResource locationResource = new LocationResource(new MozuApiContext(mTenantId,mSiteId));
            try {
                Location location =  locationResource.getLocation(strings[0]);
                return location.getName();
            } catch (Exception e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String locationName) {
            if (isCancelled()) {
                locationName = null;
            }
            if (mTextViewReference != null) {
                TextView textView = mTextViewReference.get();
                if (textView != null) {
                    textView.setText(locationName);
                }
            }
        }

    }

}
