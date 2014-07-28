package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrdersAdapter extends ArrayAdapter<Order> {

    private NumberFormat mNumberFormat;

    public OrdersAdapter(Context context) {
        super(context, R.layout.order_list_item);

        mNumberFormat = NumberFormat.getCurrencyInstance();
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

        android.text.format.DateFormat dateFormat= new android.text.format.DateFormat();
        String date = order.getSubmittedDate() != null ? dateFormat.format("MM/dd/yy  hh:mm a", new Date(order.getSubmittedDate().getMillis())).toString() : "";

        orderDate.setText(date);
        email.setText(order.getEmail());
        status.setText(order.getStatus());

        total.setText(mNumberFormat.format(order.getTotal()));

        return convertView;
    }

}
