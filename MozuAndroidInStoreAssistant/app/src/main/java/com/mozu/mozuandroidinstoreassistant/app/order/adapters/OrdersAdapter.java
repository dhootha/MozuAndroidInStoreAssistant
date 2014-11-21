package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;
import java.util.Date;

public class OrdersAdapter extends ArrayAdapter<Order> {

    private NumberFormat mNumberFormat;

    public OrdersAdapter(Context context) {
        super(context, R.layout.order_list_item);

        mNumberFormat = NumberFormat.getCurrencyInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (OrderViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_list_item, parent, false);
            viewHolder = new OrderViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        Order order = getItem(position);

        viewHolder.orderNumber.setText(String.valueOf(order.getOrderNumber()));

        String date = order.getSubmittedDate() != null ? DateFormat.format("MM/dd/yy  hh:mm a", new Date(order.getSubmittedDate().getMillis())).toString() : "";

        viewHolder.orderDate.setText(date);
        viewHolder.email.setText(order.getEmail());
        viewHolder.status.setText(order.getStatus());

        viewHolder.total.setText(mNumberFormat.format(order.getTotal()));

        return convertView;
    }


}
