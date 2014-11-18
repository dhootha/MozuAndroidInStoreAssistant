package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailPickupFulfillmentViewHolder {

    @InjectView(R.id.pickup_name) TextView pickupName;
    @InjectView(R.id.item_count) TextView itemCount;

    public OrderDetailPickupFulfillmentViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
