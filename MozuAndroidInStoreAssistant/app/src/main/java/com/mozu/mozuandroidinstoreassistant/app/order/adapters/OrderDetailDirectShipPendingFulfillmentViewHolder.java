package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailDirectShipPendingFulfillmentViewHolder {

    @InjectView(R.id.order_item_name) TextView productName;

    public OrderDetailDirectShipPendingFulfillmentViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
