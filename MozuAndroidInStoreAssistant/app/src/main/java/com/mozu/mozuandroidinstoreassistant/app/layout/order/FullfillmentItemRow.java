package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class FullfillmentItemRow extends LinearLayout implements IRowLayout {
    public FullfillmentItemRow(Context context) {
        super(context);
    }

    public FullfillmentItemRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullfillmentItemRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView mDataTextView = (TextView) findViewById(R.id.order_item_name);
        TextView mItemCode = (TextView) findViewById(R.id.order_item_code);
        TextView mFulfillmentType = (TextView) findViewById(R.id.fulfillment_type);
        TextView mLocationCode = (TextView) findViewById(R.id.location_code);
        TextView mQuantity = (TextView) findViewById(R.id.quantity);
        TextView mProductTotal = (TextView) findViewById(R.id.ordered_item_total);
        if (data instanceof FulfillmentDataItem) {
            FulfillmentDataItem fulfillmentDataItem = (FulfillmentDataItem) data;
            mDataTextView.setText(fulfillmentDataItem.getOrderItem().getProduct().getName());
            mItemCode.setText(fulfillmentDataItem.getOrderItem().getProduct().getProductCode());
            mFulfillmentType.setText(fulfillmentDataItem.getOrderItem().getFulfillmentMethod());
            mLocationCode.setText(fulfillmentDataItem.getOrderItem().getFulfillmentLocationCode());
            mQuantity.setText("" + fulfillmentDataItem.getOrderItem().getQuantity());
            mProductTotal.setText("" + fulfillmentDataItem.getOrderItem().getTotal());
        } else {
            mDataTextView.setText("N/A");
        }

    }
}
