package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.LocationInventory;
import com.mozu.api.contracts.productruntime.ProductProperty;
import com.mozu.api.contracts.productruntime.ProductPropertyValue;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class ProdDetailLocationInventoryAdapter extends ArrayAdapter<LocationInventory> {

    public ProdDetailLocationInventoryAdapter(Context context, List<LocationInventory> locationInventories) {
        super(context, R.layout.inventory_list_item);

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

        return convertView;
    }

}
