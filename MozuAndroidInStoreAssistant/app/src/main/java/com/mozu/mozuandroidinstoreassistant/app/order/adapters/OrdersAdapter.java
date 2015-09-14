package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrdersAdapter extends BaseAdapter {

    private NumberFormat mNumberFormat;
    private ArrayList<Order> mData = new ArrayList<>();

    public OrdersAdapter(Context context) {
        mNumberFormat = NumberFormat.getCurrencyInstance();
    }

    public ArrayList<Order> getData() {
        return mData;
    }

    public void setData(ArrayList<Order> data) {
        mData.clear();
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Order getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (OrderViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
            viewHolder = new OrderViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        Order order = getItem(position);

        viewHolder.orderNumber.setText(String.valueOf(order.getOrderNumber()));

        String date = order.getSubmittedDate() != null ? DateFormat.format("MM/dd/yy  hh:mm a", new Date(order.getSubmittedDate().getMillis())).toString() : "";

        viewHolder.orderDate.setText(date);
        viewHolder.paymentStatus.setText(order.getPaymentStatus());
        viewHolder.status.setText(order.getStatus());

        viewHolder.total.setText(mNumberFormat.format(order.getTotal()));

        return convertView;
    }


}
