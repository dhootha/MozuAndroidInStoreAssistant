package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class OrderDetailPackageAdapter extends ArrayAdapter<Package> {


    public OrderDetailPackageAdapter(Context context, List<Package> packages) {
        super(context, R.layout.package_list_item);

        addAll(packages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.returns_list_item, parent, false);
        }

        Package orderPackage = getItem(position);

        TextView packageName = (TextView) convertView.findViewById(R.id.package_name);
        TextView numOfItems = (TextView) convertView.findViewById(R.id.num_of_items);
        TextView trackingNumber = (TextView) convertView.findViewById(R.id.tracking_number);

        packageName.setText("Package #" + String.valueOf(position));

        if (orderPackage.getItems() != null) {
            numOfItems.setText(String.valueOf(orderPackage.getItems().size()));
        } else {
            numOfItems.setText("N/A");
        }

        trackingNumber.setText(orderPackage.getTrackingNumber());

        return convertView;
    }

}
