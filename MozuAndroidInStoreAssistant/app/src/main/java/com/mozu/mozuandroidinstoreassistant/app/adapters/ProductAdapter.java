package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.fragments.ProductDetailInventoryFragment;
import com.mozu.mozuandroidinstoreassistant.app.models.ImageURLConverter;
import com.mozu.mozuandroidinstoreassistant.app.views.CropSquareTransformation;
import com.mozu.mozuandroidinstoreassistant.app.views.RoundedTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.NumberFormat;

public class ProductAdapter extends GridToggleArrayAdapter<Product> {

    private ImageURLConverter mUrlConverter;


    private NumberFormat mNumberFormat;
    private Integer mTenantId;
    private Integer mSiteId;
    private Context mContext;

    public ProductAdapter(Context context, Integer tenantId, Integer siteId) {
        super(context, R.layout.product_grid_item, R.layout.product_list_item);

        mNumberFormat = NumberFormat.getCurrencyInstance();

        mUrlConverter = new ImageURLConverter(tenantId, siteId);
        mTenantId = tenantId;
        mSiteId = siteId;
        mContext = context;
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

            RequestCreator creator = Picasso.with(getContext())
                    .load(mUrlConverter.getFullImageUrl(product.getContent().getProductImages().get(0).getImageUrl()));

            if (!isGrid()) {
                creator = creator.transform(new RoundedTransformation());
            } else {
                creator = creator.placeholder(R.drawable.icon_noproductphoto);
            }

            creator.fit().centerCrop().into(viewHolder.productImage);
        }

        viewHolder.productSku.setText(product.getProductCode());
        viewHolder.productPrice.setText(product.getPrice() != null && product.getPrice().getPrice() != null && product.getPrice().getPrice() > 0.0 ? mNumberFormat.format(product.getPrice().getPrice()) : "");
        viewHolder.productSalePrice.setText(product.getPrice() != null && product.getPrice().getSalePrice() != null && product.getPrice().getSalePrice() > 0.0 ? mNumberFormat.format(product.getPrice().getSalePrice()) : "");
        viewHolder.productInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductDetailInventoryFragment inventoryFragment = new ProductDetailInventoryFragment();
                inventoryFragment.setProduct(product);
                inventoryFragment.setTenantId(mTenantId);
                inventoryFragment.setSiteId(mSiteId);
                inventoryFragment.show(((Activity)mContext).getFragmentManager(),"inventory_fragment");
            }
        });
        return convertView;
    }

}
