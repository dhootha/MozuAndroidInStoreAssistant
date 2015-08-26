package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.PickupFulfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class PickupFullfillmentTitleRow extends RelativeLayout implements IRowLayout {
    public PickupFullfillmentTitleRow(Context context) {
        super(context);
    }

    public PickupFullfillmentTitleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PickupFullfillmentTitleRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {

        TextView mTitleTextView = (TextView) findViewById(R.id.order_fulfillment_title);
        TextView mUnfullfilledTextView = (TextView) findViewById(R.id.unfulfilled_total);
        TextView mFulFilledTextView = (TextView) findViewById(R.id.fulfilled_total);
        TextView mTotalTextView = (TextView) findViewById(R.id.total);
        if (data instanceof PickupFulfillmentTitleDataItem) {
            PickupFulfillmentTitleDataItem dataItem = (PickupFulfillmentTitleDataItem) data;
            if (dataItem.getTitle() != null) {
                mTitleTextView.setText(dataItem.getTitle());
            }
            if (dataItem.getUnfullfilledCount() != null) {
                mUnfullfilledTextView.setText(String.valueOf(dataItem.getUnfullfilledCount()));
            }
            if (dataItem.getFullfilledCount() != null) {
                mFulFilledTextView.setText(String.valueOf(dataItem.getFullfilledCount()));
            }
            if (dataItem.getTotalCount() != null) {
                mTotalTextView.setText(String.valueOf(dataItem.getTotalCount()));
            }
        }else{
            mTitleTextView.setText(getResources().getString(R.string.not_available));
            mUnfullfilledTextView.setText(getResources().getString(R.string.not_available));
            mFulFilledTextView.setText(getResources().getString(R.string.not_available));
            mTotalTextView.setText(getResources().getString(R.string.not_available));
        }

    }
}
