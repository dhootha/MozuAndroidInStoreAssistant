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
import com.mozu.mozuandroidinstoreassistant.app.views.CropSquareTransformation;
import com.mozu.mozuandroidinstoreassistant.app.views.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

public class ProductAdapter extends GridToggleArrayAdapter<Product> {

    private ImageURLConverter mUrlConverter;

    private int mImageWidth;
    private int mImageHeight;

    private NumberFormat mNumberFormat;

    public ProductAdapter(Context context, Integer tenantId, Integer siteId) {
        super(context, R.layout.product_grid_item, R.layout.product_list_item);

        mNumberFormat = NumberFormat.getCurrencyInstance();

        mUrlConverter = new ImageURLConverter(tenantId, siteId);

        mImageWidth = (int) context.getResources().getDimension(R.dimen.product_image_list_width);
        mImageHeight = (int) context.getResources().getDimension(R.dimen.product_image_list_height);
    }

    @Override
    public long getItemId(int position) {

         return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductViewHolder viewHolder;

        if ( convertView == null ||
           ( isGrid() && convertView.getId() == getGridResource()) ||
           ( !isGrid() && convertView.getId() == getListResource()) ) {

           convertView = LayoutInflater.from(getContext()).inflate(getCurrentResource(), parent, false);
           viewHolder = new ProductViewHolder(convertView);
           convertView.setTag(viewHolder);
        } else {
            viewHolder = (ProductViewHolder) convertView.getTag();
        }

        final Product product = getItem(position);

        viewHolder.productName.setText(product.getContent().getProductName());

        //load image asynchronously into the view
        if (product.getContent().getProductImages() != null && product.getContent().getProductImages().size() > 0) {

            Picasso.with(getContext())
                    .load(mUrlConverter.getFullImageUrl(product.getContent().getProductImages().get(0).getImageUrl()))
                    .placeholder(R.drawable.icon_noproductphoto)
                    .fit().centerCrop()
                    .into(viewHolder.productImage);
        }

        viewHolder.productSku.setText(product.getProductCode());
        viewHolder.productPrice.setText(product.getPrice() != null && product.getPrice().getPrice() != null && product.getPrice().getPrice() > 0.0 ? mNumberFormat.format(product.getPrice().getPrice()) : "");
        viewHolder.productSalePrice.setText(product.getPrice() != null && product.getPrice().getSalePrice() != null && product.getPrice().getSalePrice() > 0.0 ? mNumberFormat.format(product.getPrice().getSalePrice())  : "");

        return convertView;
    }

}
