package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProductViewHolder {

    @InjectView(R.id.product_name) TextView productName;
    @InjectView(R.id.product_image) ImageView productImage;
    @InjectView(R.id.product_sku) TextView productSku;
    @InjectView(R.id.product_price) TextView productPrice;
    @InjectView(R.id.product_sale_price) TextView productSalePrice;

    public ProductViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
