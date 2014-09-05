package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class OrderDetailPickupFulfillmentAdapter extends ArrayAdapter<Pickup> {

    public static final String FULFILLED = "fulfilled";

    public OrderDetailPickupFulfillmentAdapter(Context context, List<Pickup> pickups) {
        super(context, R.layout.fulfillment_pickup_list_item);

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

        setCellType(viewHolder.cellType, pickup);

        viewHolder.mainLabel.setText(getContext().getResources().getString(R.string.fulfillment_pickup_number) + String.valueOf(position + 1));

        setRemainingFields(viewHolder, pickup);

        return convertView;
    }

    private void setCellType(TextView view, Pickup item) {

        //items are not packaged
        if (!item.getStatus().equalsIgnoreCase(FULFILLED)) {
            view.setText(getContext().getResources().getString(R.string.pickup_fulfillment_pending));

            return;
        }

        view.setText(getContext().getResources().getString(R.string.pickup_fulfillment_fulfilled));
    }


    private void setRemainingFields(OrderDetailPickupFulfillmentViewHolder viewHolder, Pickup item) {

        if (item.getItems() != null) {
            viewHolder.itemCount.setText(String.valueOf(item.getItems().size()) + " " + getContext().getString(R.string.fulfillment_items_label));
        }

    }
}
