package com.mozu.mozuandroidinstoreassistant.app.layout.customer;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerOverviewDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class CustomerAccountInfoHeader extends LinearLayout implements IRowLayout {
    public CustomerAccountInfoHeader(Context context) {
        super(context);
    }

    public CustomerAccountInfoHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerAccountInfoHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView header1 = (TextView) findViewById(R.id.header1);
        TextView value1 = (TextView) findViewById(R.id.value1);
        TextView header2 = (TextView) findViewById(R.id.header2);
        TextView value2 = (TextView) findViewById(R.id.value2);
        if (data instanceof CustomerOverviewDataItem) {
            CustomerOverviewDataItem info = (CustomerOverviewDataItem) data;

            if (info.getHeader1() != null) {
                header1.setText(info.getHeader1());
            } else {
                header1.setText(getResources().getString(R.string.not_available));
            }
            if (info.getHeader2() == null) {
                header2.setText(getResources().getString(R.string.not_available));
            } else {
                header2.setText(info.getHeader2());
            }
            if (!TextUtils.isEmpty(info.getValue1())) {
                value1.setText(info.getValue1());
            } else {
                value1.setText(getResources().getString(R.string.not_available));
            }

            if (!TextUtils.isEmpty(info.getValue2())) {
                value2.setText(info.getValue2());
            } else {
                value2.setText(getResources().getString(R.string.not_available));
            }

        } else {
            header1.setText(getResources().getString(R.string.not_available));
            header2.setText(getResources().getString(R.string.not_available));
            value1.setText(getResources().getString(R.string.not_available));
            value2.setText(getResources().getString(R.string.not_available));
        }

    }
}
