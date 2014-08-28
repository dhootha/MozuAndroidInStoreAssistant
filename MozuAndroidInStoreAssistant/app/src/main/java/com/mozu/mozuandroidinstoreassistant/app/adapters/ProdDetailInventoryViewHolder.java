package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.views.NetworkLocationNameTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProdDetailInventoryViewHolder {

    @InjectView(R.id.inventory_code) TextView code;
    @InjectView(R.id.inventory_name) NetworkLocationNameTextView name;
    @InjectView(R.id.inventory_available) TextView available;
    @InjectView(R.id.inventory_on_hand) TextView onHand;
    @InjectView(R.id.inventory_on_reserve) TextView onReserve;

    public ProdDetailInventoryViewHolder(View view, Integer tenantId, Integer siteId) {
        ButterKnife.inject(this, view);

        name.init(tenantId, siteId);
    }

}
