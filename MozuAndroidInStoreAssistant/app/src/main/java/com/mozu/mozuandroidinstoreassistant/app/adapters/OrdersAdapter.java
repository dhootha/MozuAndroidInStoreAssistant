package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;

public class OrdersAdapter extends ArrayAdapter<Order> {

    public OrdersAdapter(Context context) {
        super(context, R.layout.order_list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_list_item, parent, false);
        }

        Order order = getItem(position);

        TextView orderNo = (TextView) convertView.findViewById(R.id.order_number);
        TextView orderDate = (TextView) convertView.findViewById(R.id.order_date);
        TextView email = (TextView) convertView.findViewById(R.id.order_email);
        TextView status = (TextView) convertView.findViewById(R.id.order_status);
        TextView total = (TextView) convertView.findViewById(R.id.order_total);

        orderNo.setText(String.valueOf(order.getOrderNumber()));
        orderDate.setText(order.getSubmittedDate() != null ? order.getSubmittedDate().toString() : "");
        email.setText(order.getEmail());
        status.setText(order.getStatus());
        total.setText(String.valueOf(order.getTotal()));

        return convertView;
    }

}
