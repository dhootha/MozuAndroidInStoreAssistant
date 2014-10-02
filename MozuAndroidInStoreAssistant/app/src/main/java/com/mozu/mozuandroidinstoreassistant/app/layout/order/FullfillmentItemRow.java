package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentDataItem;
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
        if (data instanceof FullfillmentDataItem) {
            FullfillmentDataItem fullfillmentDataItem = (FullfillmentDataItem) data;
            mDataTextView.setText(fullfillmentDataItem.getOrderItem().getProduct().getName());
        } else {
            mDataTextView.setText("N/A");
        }

    }
}
