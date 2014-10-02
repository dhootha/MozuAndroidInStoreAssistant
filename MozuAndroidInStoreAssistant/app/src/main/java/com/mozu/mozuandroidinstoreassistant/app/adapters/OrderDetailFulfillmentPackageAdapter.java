package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;

import java.util.List;

public class OrderDetailFulfillmentPackageAdapter extends ArrayAdapter<FulfillmentItem> {

    public OrderDetailFulfillmentPackageAdapter(Context context, List<FulfillmentItem> fulfillmentItems) {
        super(context, R.layout.fulfillment_direct_ship_package_list_item);

        addAll(fulfillmentItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDetailDirectShipPackageViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fulfillment_direct_ship_package_list_item, parent, false);
            viewHolder = new OrderDetailDirectShipPackageViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderDetailDirectShipPackageViewHolder) convertView.getTag();
        }

        FulfillmentItem orderPackage = getItem(position);

        viewHolder.packageName.setText(orderPackage.getPackageNumber());


        setRemainingFields(viewHolder, orderPackage);

        return convertView;
    }

    private void setRemainingFields(OrderDetailDirectShipPackageViewHolder viewHolder, FulfillmentItem item) {

        if (item.getOrderPackage() != null && item.getOrderPackage().getItems() != null) {
            viewHolder.packageCount.setText(String.valueOf(item.getOrderPackage().getItems().size()) + " " + getContext().getString(R.string.fulfillment_items_label));
        }

        if (item.getOrderPackage() != null) {
            viewHolder.trackingNumber.setText(item.getOrderPackage().getTrackingNumber());
        }

        //shipment tracking number should supercede package tracking number
        if (item.getShipment() != null) {
            viewHolder.trackingNumber.setText(item.getShipment().getTrackingNumber());
        }

    }
}
