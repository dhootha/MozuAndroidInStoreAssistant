package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.ImageURLConverter;
import com.mozu.mozuandroidinstoreassistant.app.tasks.InventoryButtonClickListener;
import com.mozu.mozuandroidinstoreassistant.app.views.RoundedTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.NumberFormat;

public class ProductAdapter extends GridToggleArrayAdapter<Product> {

    private ImageURLConverter mUrlConverter;


    private NumberFormat mNumberFormat;
    private InventoryButtonClickListener mInventoryClickListener;
    private Context mContext;

    public ProductAdapter(Context context, Integer tenantId, Integer siteId,InventoryButtonClickListener inventoryClickListener) {
        super(context, R.layout.product_grid_item, R.layout.product_list_item);
        mContext = context;
        mNumberFormat = NumberFormat.getCurrencyInstance();

        mUrlConverter = new ImageURLConverter(tenantId, siteId);
        mInventoryClickListener = inventoryClickListener;
    }

    @Override
    public long getItemId(int position) {

         return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ProductViewHolder viewHolder;

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
        viewHolder.productImage.setImageResource(R.drawable.icon_noproductphoto);
        viewHolder.productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //load image asynchronously into the view
        if (product.getContent().getProductImages() != null && product.getContent().getProductImages().size() > 0) {

            RequestCreator creator = Picasso.with(getContext())
                    .load(mUrlConverter.getFullImageUrl(product.getContent().getProductImages().get(0).getImageUrl()));

            if (!isGrid()) {
                creator = creator.transform(new RoundedTransformation()).placeholder(R.drawable.icon_noproductphoto);
                int dimenWidth = getContext().getResources().getDimensionPixelSize(R.dimen.product_list_item_width);
                int dimenHeight = getContext().getResources().getDimensionPixelSize(R.dimen.product_list_item_height);
                creator = creator.transform(new RoundedTransformation()).placeholder(R.drawable.icon_nocategoryphoto).resize(dimenWidth, dimenHeight).centerInside();
            } else {
                int dimenWidth = getContext().getResources().getDimensionPixelSize(R.dimen.category_grid_image_width);
                int dimenHeight = getContext().getResources().getDimensionPixelSize(R.dimen.category_grid_image_height);
                creator = creator.placeholder(R.drawable.icon_noproductphoto).resize(dimenWidth, dimenHeight).centerInside();
            }

            creator.into(viewHolder.productImage, new Callback() {

                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) viewHolder.productImage.getDrawable()).getBitmap();
                    viewHolder.productImage.setBackgroundColor(bitmap.getPixel(0, 0));
                    viewHolder.productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    viewHolder.productLoading.success();
                }

                @Override
                public void onError() {
                    viewHolder.productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    viewHolder.productLoading.success();
                }

            });
        }else{
            viewHolder.productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.productLoading.success();
        }

        viewHolder.productSku.setText(product.getProductCode());
        if(product.getPrice() != null && product.getPrice().getSalePrice() != null){
            if(isGrid()) {
                viewHolder.productPrice.setVisibility(View.GONE);
            }else {
                viewHolder.productPrice.setVisibility(View.VISIBLE);
            }
            viewHolder.productSalePrice.setVisibility(View.VISIBLE);
        }else{
            viewHolder.productPrice.setVisibility(View.VISIBLE);
            if (isGrid()) {
                viewHolder.productSalePrice.setVisibility(View.GONE);
            }else {
                viewHolder.productSalePrice.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.productSalePrice.setText(product.getPrice() != null && product.getPrice().getSalePrice() != null && product.getPrice().getSalePrice() > 0.0 ? mNumberFormat.format(product.getPrice().getSalePrice()) : "");
        viewHolder.productPrice.setText(product.getPrice() != null && product.getPrice().getPrice() != null && product.getPrice().getPrice() > 0.0 ? mNumberFormat.format(product.getPrice().getPrice()) : "");

        viewHolder.productInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGrid()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setItems(R.array.product_overflow_options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                mInventoryClickListener.onClick(product);
                            }
                        }
                    });
                    Dialog dialog = builder.create();
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                    WindowManager.LayoutParams lParams = dialog.getWindow().getAttributes();
                    int[] coordinates = new int[]{0, 0};
                    lParams.gravity = Gravity.TOP | Gravity.LEFT;
                    view.getLocationOnScreen(coordinates);
                    lParams.x = (int) (coordinates[0] - view.getX());
                    lParams.y = (int) (coordinates[1] - view.getY());
                    lParams.width = 600;
                    lParams.height = 400;
                    dialog.getWindow().setAttributes(lParams);

                } else {
                    mInventoryClickListener.onClick(product);
                }
            }
        });
        return convertView;
    }

}
