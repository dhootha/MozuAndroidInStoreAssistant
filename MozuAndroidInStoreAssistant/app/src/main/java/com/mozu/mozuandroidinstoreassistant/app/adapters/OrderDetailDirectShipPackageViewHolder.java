package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailDirectShipPackageViewHolder {

    @InjectView(R.id.package_name) TextView packageName;
    @InjectView(R.id.package_count) TextView packageCount;
    @InjectView(R.id.tracking_number) TextView trackingNumber;

    public OrderDetailDirectShipPackageViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
