package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class FullfillmentBottomRow extends LinearLayout implements IRowLayout {

    public FullfillmentBottomRow(Context context) {
        super(context);
    }

    public FullfillmentBottomRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullfillmentBottomRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        //todo hook into spinner
    }
}
