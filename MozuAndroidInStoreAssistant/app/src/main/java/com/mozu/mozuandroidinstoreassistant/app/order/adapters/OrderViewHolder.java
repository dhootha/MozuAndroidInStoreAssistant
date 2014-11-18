package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderViewHolder {

    @InjectView(R.id.order_number) TextView orderNumber;
    @InjectView(R.id.order_date) TextView orderDate;
    @InjectView(R.id.order_email) TextView email;
    @InjectView(R.id.order_status) TextView status;
    @InjectView(R.id.order_total) TextView total;

    public OrderViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
