package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.View;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.views.NetworkProductNameTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailPackageItemViewHolder {

    @InjectView(R.id.package_item_code) TextView code;
    @InjectView(R.id.package_item_product) NetworkProductNameTextView productName;
    @InjectView(R.id.package_item_quantity) TextView quantity;

    public OrderDetailPackageItemViewHolder(View view, Integer tenantId, Integer siteId) {
        ButterKnife.inject(this, view);

        productName.init(tenantId, siteId);
    }

}
