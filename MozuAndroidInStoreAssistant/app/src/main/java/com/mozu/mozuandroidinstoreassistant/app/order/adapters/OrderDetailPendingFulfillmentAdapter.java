package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class OrderDetailPendingFulfillmentAdapter extends ArrayAdapter<OrderItem> {

    public OrderDetailPendingFulfillmentAdapter(Context context, List<OrderItem> orderItems) {
        super(context, R.layout.fulfillment_direct_ship_pending_list_item);

        addAll(orderItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDetailDirectShipPendingFulfillmentViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fulfillment_direct_ship_pending_list_item, parent, false);
            viewHolder = new OrderDetailDirectShipPendingFulfillmentViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderDetailDirectShipPendingFulfillmentViewHolder) convertView.getTag();
        }

        OrderItem orderItem = getItem(position);

        if (orderItem.getProduct() != null) {
            viewHolder.productName.setText(orderItem.getProduct().getName());
        } else {
            viewHolder.productName.setText("N/A");
        }

        return convertView;
    }

}
