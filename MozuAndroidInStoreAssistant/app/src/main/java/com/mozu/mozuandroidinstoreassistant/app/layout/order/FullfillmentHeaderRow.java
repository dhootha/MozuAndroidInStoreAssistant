package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentCategoryHeaderDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class FullfillmentHeaderRow extends LinearLayout implements IRowLayout {
    public FullfillmentHeaderRow(Context context) {
        super(context);
    }

    public FullfillmentHeaderRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullfillmentHeaderRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView headerTextView = (TextView) findViewById(R.id.fulfillment_header);
        if (data instanceof FullfillmentCategoryHeaderDataItem) {
            FullfillmentCategoryHeaderDataItem dataItem = (FullfillmentCategoryHeaderDataItem) data;
            headerTextView.setText(dataItem.getHeaderName());
        } else {
            headerTextView.setText("N/A");
        }
    }
}
