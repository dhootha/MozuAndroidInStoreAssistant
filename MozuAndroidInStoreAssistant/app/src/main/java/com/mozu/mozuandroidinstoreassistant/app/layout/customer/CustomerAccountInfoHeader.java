package com.mozu.mozuandroidinstoreassistant.app.layout.customer;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.PrimaryAccountInfo;
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
        TextView customerSince = (TextView) findViewById(R.id.customer_since_value);
        TextView livetimeValue = (TextView) findViewById(R.id.customer_lifetime_value);
        TextView totalVisits = (TextView)findViewById(R.id.total_visits_value);
        TextView fullfilledOrders = (TextView)findViewById(R.id.fullfilled_orders_value);
        TextView storeCredits = (TextView)findViewById(R.id.store_credits_value);
        if (data instanceof PrimaryAccountInfo) {
            PrimaryAccountInfo info = (PrimaryAccountInfo) data;
            if (info.getCustomerSince() != null) {
                customerSince.setText(info.getCustomerSince());
            } else {
                customerSince.setText("N/A");
            }
            if (info.getLiveTimeValue() == null) {
                livetimeValue.setText("N/A");
            } else {
                livetimeValue.setText(String.valueOf(info.getLiveTimeValue()));
            }
            if (info.getTotalVisits() != null) {
                totalVisits.setText(String.valueOf(info.getTotalVisits()));
            } else {
                totalVisits.setText("N/A");
            }

            if (info.getTotalOrders() != null) {
                fullfilledOrders.setText(String.valueOf(info.getTotalOrders()));
            } else {
                fullfilledOrders.setText("N/A");
            }
            if (info.getStoreCredits() != null) {
                storeCredits.setText(info.getStoreCredits());
            } else {
                storeCredits.setText("N/A");
            }
        } else {
            customerSince.setText("N/A");
            livetimeValue.setText("N/A");
            totalVisits.setText("N/A");
            fullfilledOrders.setText("N/A");
            storeCredits.setText("N/A");
        }

    }
}
