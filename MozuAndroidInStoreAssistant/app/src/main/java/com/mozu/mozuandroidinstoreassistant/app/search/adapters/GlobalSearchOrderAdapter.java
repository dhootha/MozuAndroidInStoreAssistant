package com.mozu.mozuandroidinstoreassistant.app.search.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;


public class GlobalSearchOrderAdapter extends BaseAdapter {

    private OrderCollection mOrderCollection;
    public GlobalSearchOrderAdapter(OrderCollection orderCollection){
        mOrderCollection = orderCollection;
    }
    @Override
    public int getCount() {
        if(mOrderCollection.getItems() != null){
            return mOrderCollection.getItems().size();
        }
        return 0;
    }

    public void setData(OrderCollection orderCollection){
        mOrderCollection = orderCollection;
    }
    @Override
    public Order getItem(int i) {
        if (mOrderCollection.getItems() != null) {
            return mOrderCollection.getItems().get(i);
        } else {

            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GlobalSearchOrderViewHolder viewHolder;
        View v;
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.globalsearch_order_item, parent,false);
            viewHolder = new GlobalSearchOrderViewHolder();
            viewHolder.orderId = (TextView) convertview.findViewById(R.id.orderId);
            viewHolder.orderName = (TextView) convertview.findViewById(R.id.orderName);
            viewHolder.orderStatus = (TextView)convertview.findViewById(R.id.orderStatus);
            viewHolder.orderPrice = (TextView)convertview.findViewById(R.id.orderPrice);
            convertview.setTag(viewHolder);
        }
        else{
            viewHolder = (GlobalSearchOrderViewHolder) convertview.getTag();
        }

        Order order = getItem(i);
        if (order != null) {
            viewHolder.orderId.setText(order.getOrderNumber().toString());
            if(TextUtils.isEmpty(order.getEmail())){
                viewHolder.orderName.setText("N/A");
            }else {
                viewHolder.orderName.setText(order.getEmail());
            }
            viewHolder.orderPrice.setText( NumberFormat.getCurrencyInstance().format(order.getTotal()));
            viewHolder.orderStatus.setText(order.getStatus());
        }
        return convertview;
    }

    static class GlobalSearchOrderViewHolder{
        TextView orderId;
        TextView orderName;
        TextView orderStatus;
        TextView orderPrice;
    }


}
