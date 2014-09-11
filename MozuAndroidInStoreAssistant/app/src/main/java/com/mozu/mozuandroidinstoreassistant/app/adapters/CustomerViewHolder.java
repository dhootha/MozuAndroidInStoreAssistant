package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomerViewHolder {

    @InjectView(R.id.customer_number) TextView customerNumber;
    @InjectView(R.id.customer_last_name) TextView customerLastName;
    @InjectView(R.id.customer_first_name) TextView customerFirstName;
    @InjectView(R.id.customer_email) TextView customerEmail;
    @InjectView(R.id.customer_lifetime_value) TextView customerLifetimeValue;

    public CustomerViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
