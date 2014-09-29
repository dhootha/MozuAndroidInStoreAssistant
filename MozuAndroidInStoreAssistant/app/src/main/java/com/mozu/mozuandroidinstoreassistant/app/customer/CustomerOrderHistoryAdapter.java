package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;

import java.text.NumberFormat;
import java.util.List;

public class CustomerOrderHistoryAdapter extends BaseAdapter {

    private List<Order> mData;

    public CustomerOrderHistoryAdapter(List<Order> data){
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
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order order = getItem(position);
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            view = inflater.inflate(R.layout.customer_orderhistory_item,null);
        } else {
            view = convertView;
        }
        TextView orderNum = (TextView)view.findViewById(R.id.customer_order_num_value);
        TextView orderDate = (TextView)view.findViewById(R.id.customer_order_date_value);
        TextView orderStatus = (TextView)view.findViewById(R.id.customer_order_status_value);
        TextView orderAmount = (TextView)view.findViewById(R.id.customer_order_amount_value);
        orderNum.setText(String.valueOf(order.getOrderNumber()));
        if (order.getSubmittedDate() != null) {
            orderDate.setText(DateUtils.getFormattedDateTime(order.getSubmittedDate().getMillis()));
        } else {
            orderDate.setText("N/A");
        }
        if (order.getStatus() != null) {
            orderStatus.setText(order.getStatus());
        } else {
            orderStatus.setText("N/A");
        }
        orderAmount.setText(NumberFormat.getCurrencyInstance().format(order.getTotal()));
        return view;
    }

    public void setData(List<Order> data) {
        mData = data;
    }


}
