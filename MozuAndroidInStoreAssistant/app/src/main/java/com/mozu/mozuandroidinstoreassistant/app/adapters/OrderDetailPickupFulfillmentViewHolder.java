package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailPickupFulfillmentViewHolder {

    @InjectView(R.id.pickup_fulfillment_cell_type) TextView cellType;
    @InjectView(R.id.pickup_fulfillment_main_label) TextView mainLabel;
    @InjectView(R.id.pickup_fulfillment_item_count) TextView itemCount;

    public OrderDetailPickupFulfillmentViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
