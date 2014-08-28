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
import com.mozu.mozuandroidinstoreassistant.app.views.NetworkLocationNameTextView;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.InjectView;

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
        ProdDetailInventoryViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_list_item, parent, false);
            viewHolder = new ProdDetailInventoryViewHolder(convertView, mTenantId, mSiteId);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ProdDetailInventoryViewHolder) convertView.getTag();
        }

        LocationInventory locationInventory = getItem(position);

        viewHolder.code.setText(locationInventory.getLocationCode());
        viewHolder.available.setText( String.valueOf(locationInventory.getStockAvailable()));
        viewHolder.onHand.setText(locationInventory.getStockOnHand().toString());
        viewHolder.onReserve.setText(locationInventory.getStockOnBackOrder().toString());
        viewHolder.name.loadName(locationInventory.getLocationCode());

        return convertView;
    }

}
