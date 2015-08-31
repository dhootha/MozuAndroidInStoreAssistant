package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShipmentFulfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class FullfillmentTitleRow extends RelativeLayout implements IRowLayout {
    public FullfillmentTitleRow(Context context) {
        super(context);
    }

    public FullfillmentTitleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullfillmentTitleRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {

        TextView mTitleTextView = (TextView) findViewById(R.id.order_fulfillment_title);
        TextView mPendingTextView = (TextView) findViewById(R.id.shipment_pending_total);
        TextView mFulFilledTextView = (TextView) findViewById(R.id.shipment_fulfilled_total);
        TextView mTotalTextView = (TextView) findViewById(R.id.shipment_total);
        if (data instanceof ShipmentFulfillmentTitleDataItem) {
            ShipmentFulfillmentTitleDataItem dataItem = (ShipmentFulfillmentTitleDataItem) data;
            if (dataItem.getTitle() != null) {
                mTitleTextView.setText(dataItem.getTitle());
            }
            if (dataItem.getUnShippedCount() != null) {
                mPendingTextView.setText(String.valueOf(dataItem.getUnShippedCount()));
            }
            if (dataItem.getFullfilledCount() != null) {
                mFulFilledTextView.setText(String.valueOf(dataItem.getFullfilledCount()));
            }
            if (dataItem.getTotalCount() != null) {
                mTotalTextView.setText(String.valueOf(dataItem.getTotalCount()));
            }
        }else{
            mTitleTextView.setText("N/A");
            mPendingTextView.setText("N/A");
            mFulFilledTextView.setText("N/A");
            mTotalTextView.setText("N/A");
        }

    }
}
