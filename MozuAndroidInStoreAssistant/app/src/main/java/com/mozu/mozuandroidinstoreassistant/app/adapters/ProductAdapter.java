package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.ImageURLConverter;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

public class ProductAdapter extends ArrayAdapter<Product> {

    private ImageURLConverter mUrlConverter;

    public ProductAdapter(Context context, Integer tenantId, Integer siteId) {
        super(context, R.layout.product_grid_item);

        mUrlConverter = new ImageURLConverter(tenantId, siteId);
    }

    @Override
    public long getItemId(int position) {

         return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_grid_item, parent, false);
        }

        Product product = getItem(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.product_name);
        nameTextView.setText(product.getContent().getProductName());

        ImageView productImageView = (ImageView) convertView.findViewById(R.id.product_image);
        productImageView.setImageResource(R.drawable.default_background);

        //load image asynchronously into the view
        if (product.getContent().getProductImages() != null && product.getContent().getProductImages().size() > 0) {
            Picasso.with(getContext())
                    .load(mUrlConverter.getFullImageUrl(product.getContent().getProductImages().get(0).getImageUrl()))
                    .fit()
                    .into(productImageView);
        }

        TextView skuTextView = (TextView) convertView.findViewById(R.id.product_sku);
        skuTextView.setText(product.getProductCode());

        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();

        TextView priceTextView = (TextView) convertView.findViewById(R.id.product_price);
        priceTextView.setText(product.getPrice() != null && product.getPrice().getPrice() != null && product.getPrice().getPrice() > 0.0 ? defaultFormat.format(product.getPrice().getPrice()) : "");

        TextView salePriceTextView = (TextView) convertView.findViewById(R.id.product_sale_price);
        salePriceTextView.setText(product.getPrice() != null && product.getPrice().getSalePrice() != null && product.getPrice().getSalePrice() > 0.0 ? defaultFormat.format(product.getPrice().getSalePrice())  : "");

        return convertView;
    }

}