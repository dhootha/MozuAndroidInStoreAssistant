package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailDirectShipFulfillmentViewHolder {

    @InjectView(R.id.direct_ship_fulfillment_cell_type) TextView cellType;
    @InjectView(R.id.direct_ship_fulfillment_main_label) TextView mainLabel;
    @InjectView(R.id.direct_ship_fulfillment_package_count) TextView itemCount;
    @InjectView(R.id.direct_ship_fulfillment_tracking_number) TextView trackingNumber;

    public OrderDetailDirectShipFulfillmentViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
