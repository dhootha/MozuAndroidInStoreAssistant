package com.mozu.mozuandroidinstoreassistant.app.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class EmptyRow extends LinearLayout implements IRowLayout {
    public EmptyRow(Context context) {
        super(context);
    }

    public EmptyRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
    String s= "100";
    }
}
