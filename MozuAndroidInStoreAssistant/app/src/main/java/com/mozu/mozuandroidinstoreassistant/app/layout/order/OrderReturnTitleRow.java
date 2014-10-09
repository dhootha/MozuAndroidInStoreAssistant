package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderReturnTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class OrderReturnTitleRow extends LinearLayout implements IRowLayout {
    public OrderReturnTitleRow(Context context) {
        super(context);
    }

    public OrderReturnTitleRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderReturnTitleRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView titleText = (TextView) findViewById(R.id.header_text);
        if (data instanceof OrderReturnTitleDataItem) {
            OrderReturnTitleDataItem orderReturnTitleDataItem = (OrderReturnTitleDataItem) data;
            titleText.setText(orderReturnTitleDataItem.getTitle());
        }
    }
}
