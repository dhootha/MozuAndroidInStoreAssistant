package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderRefundDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderRefundHeaderItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class OrderRefundHeaderItemRow extends LinearLayout implements IRowLayout {
    public OrderRefundHeaderItemRow(Context context) {
        super(context);
    }

    public OrderRefundHeaderItemRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderRefundHeaderItemRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {

    }
}
