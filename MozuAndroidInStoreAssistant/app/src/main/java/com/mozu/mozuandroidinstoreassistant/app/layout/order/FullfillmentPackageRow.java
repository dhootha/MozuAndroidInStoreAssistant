package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentPackageDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class FullfillmentPackageRow extends LinearLayout implements IRowLayout {
    public FullfillmentPackageRow(Context context) {
        super(context);
    }

    public FullfillmentPackageRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullfillmentPackageRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView mPackageName = (TextView) findViewById(R.id.package_name);
        TextView mPackageCount = (TextView) findViewById(R.id.package_count);
        TextView mPackageTracking = (TextView) findViewById(R.id.package_tracking_number);

        if (data instanceof FullfillmentPackageDataItem) {
            FullfillmentPackageDataItem fullfillmentPackageDataItem = (FullfillmentPackageDataItem) data;
            mPackageName.setText(fullfillmentPackageDataItem.getPackageName());
            mPackageCount.setText(String.valueOf(fullfillmentPackageDataItem.getPackageCount()+ " " + getContext().getString(R.string.fulfillment_items_label)));
            if (fullfillmentPackageDataItem.getPackageTrackingNumber() != null) {
                mPackageTracking.setText(fullfillmentPackageDataItem.getPackageTrackingNumber());
            }
            if (fullfillmentPackageDataItem.getShipmentTrackingNumber() != null) {
                mPackageTracking.setText(fullfillmentPackageDataItem.getShipmentTrackingNumber());
            }

        } else {
            mPackageName.setText("N/A");
            mPackageCount.setText("N/A");
            mPackageTracking.setText("N/A");
        }

    }
}
