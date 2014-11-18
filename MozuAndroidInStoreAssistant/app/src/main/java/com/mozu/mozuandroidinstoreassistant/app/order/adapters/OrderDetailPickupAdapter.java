package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class OrderDetailPickupAdapter extends ArrayAdapter<Pickup> {

    private int mFirstPickupCount = 1;

    public OrderDetailPickupAdapter(Context context, List<Pickup> pickups, int firstPickupCount) {
        super(context, R.layout.fulfillment_pickup_list_item);

        mFirstPickupCount = firstPickupCount;

        addAll(pickups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDetailPickupFulfillmentViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fulfillment_pickup_list_item, parent, false);
            viewHolder = new OrderDetailPickupFulfillmentViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderDetailPickupFulfillmentViewHolder) convertView.getTag();
        }

        Pickup pickup = getItem(position);

        viewHolder.pickupName.setText(getContext().getResources().getString(R.string.fulfillment_pickup_number) + String.valueOf(position + mFirstPickupCount));

        if (pickup.getItems() != null) {
            viewHolder.itemCount.setText(String.valueOf(pickup.getItems().size()) + " " + getContext().getString(R.string.fulfillment_items_label));
        }

        return convertView;
    }

}
