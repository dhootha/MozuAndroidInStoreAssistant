package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.CouponsRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderHeaderItemRow;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderItemRow;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderTotalRow;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShippingItemRow;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.IEditMode;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.NewOrderCouponLayout;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.NewOrderShippingItemLayout;

import java.util.ArrayList;
import java.util.List;


public class NewOrderProductAdapter extends BaseAdapter {

    private boolean editMode;
    private List<IData> mData;
    private NewOrderShippingItemLayout.OrderUpdateListener mUpdateListener;

    public NewOrderProductAdapter(NewOrderShippingItemLayout.OrderUpdateListener updateListener) {
        mUpdateListener = updateListener;
        mData = new ArrayList<IData>();
        mData.add(new OrderHeaderItemRow());
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    @Override
    public boolean isEnabled(int position) {
        return getRowType(position) == RowType.ORDER_ITEM_ROW;
    }

    public void addData(Order order) {
        mData.clear();
        mData.add(new OrderHeaderItemRow());
        for (OrderItem item : order.getItems()) {
            mData.add(new OrderItemRow(item));
        }
        if (order.getItems().size() > 0) {
            mData.add(new ShippingItemRow(order, order.getFulfillmentInfo()));
            mData.add(new CouponsRowItem(order));
            mData.add(new OrderTotalRow(order));
        }
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
        } else if (dataItem instanceof OrderTotalRow) {
            return RowType.TOTAL_ROW;
        } else if (dataItem instanceof OrderHeaderItemRow) {
            return RowType.ORDER_HEADER_ROW;
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
        RowType rowType = getRowType(position);
        if (convertView == null) {
            if (rowType == RowType.ORDER_ITEM_ROW) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_order_product_item, viewGroup, false);
            } else if (rowType == RowType.COUPON_ROW) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_order_coupon_item, viewGroup, false);
                ((NewOrderCouponLayout) convertView).setUpdateListener(mUpdateListener);
            } else if (rowType == RowType.SHIPPING_ROW) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_order_shipping_item, viewGroup, false);
                ((NewOrderShippingItemLayout) convertView).setOrderUpdateListener(mUpdateListener);
            } else if (rowType == RowType.TOTAL_ROW) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_order_summary_item, viewGroup, false);
            } else if (rowType == RowType.ORDER_HEADER_ROW) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.neworder_header_layout, viewGroup, false);
                return convertView;
            }
        }
        IData orderItem = getItem(position);

        if (convertView instanceof IRowLayout) {
            ((IRowLayout) convertView).bindData(orderItem);
        }
        if (convertView instanceof IEditMode) {
            ((IEditMode) convertView).setEditMode(editMode);
        }
        return convertView;
    }

    public enum RowType {
        ORDER_HEADER_ROW,
        ORDER_ITEM_ROW,
        COUPON_ROW,
        SHIPPING_ROW,
        TOTAL_ROW,
        EMPTY_ROW
    }


}
