package com.mozu.mozuandroidinstoreassistant.app.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.CouponsRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderItemRow;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShippingItemRow;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.NewOrderItemLayout;

import java.util.ArrayList;
import java.util.List;


public class NewOrderProductAdapter extends BaseAdapter {

    public enum RowType {
        ORDER_ITEM_ROW,
        COUPON_ROW,
        SHIPPING_ROW,
        EMPTY_ROW
    }

    private List<IData> mData;

    @Override
    public boolean isEnabled(int position) {
        return getRowType(position) == RowType.ORDER_ITEM_ROW;
    }

    public void addData(Order order) {
        mData.clear();
        for (OrderItem item : order.getItems()) {
            mData.add(new OrderItemRow(item));
        }
        mData.add(new ShippingItemRow(order.getShippingAdjustment(), order.getFulfillmentInfo().getShippingMethodCode()));
    }

    public NewOrderProductAdapter() {
        mData = new ArrayList<IData>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public RowType getRowType(int position) {
        IData dataItem = getItem(position);
        if (dataItem instanceof OrderItemRow) {
            return RowType.ORDER_ITEM_ROW;
        } else if (dataItem instanceof CouponsRowItem) {
            return RowType.COUPON_ROW;
        } else if (dataItem instanceof ShippingItemRow) {
            return RowType.SHIPPING_ROW;
        } else {
            return RowType.EMPTY_ROW;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getRowType(position).ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }


    @Override
    public IData getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            if (getRowType(position) == RowType.ORDER_ITEM_ROW) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_order_product_item, viewGroup, false);
            } else if (getRowType(position) == RowType.COUPON_ROW) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_order_product_item, viewGroup, false);
            } else {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_order_shipping_item, viewGroup, false);
                Spinner spinner = (Spinner) convertView.findViewById(R.id.shipping_spinner);
                spinner.setAdapter();
                (NewOrderItemLayout).

            }
        }
        IData orderItem = getItem(position);
        ((IRowLayout) convertView).bindData(orderItem);
        return convertView;
    }


}
