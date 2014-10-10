package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class OrderReturnHeaderItemRow extends LinearLayout implements IRowLayout{
    public OrderReturnHeaderItemRow(Context context) {
        super(context);
    }

    public OrderReturnHeaderItemRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderReturnHeaderItemRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {

    }
}