package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Category;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.LoadingDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.category.CategoryDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.product.ProductDataItem;
import com.mozu.mozuandroidinstoreassistant.app.models.ImageURLConverter;
import com.mozu.mozuandroidinstoreassistant.app.tasks.InventoryButtonClickListener;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;
import com.mozu.mozuandroidinstoreassistant.app.views.RoundedTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CategoryProductAdapter extends BaseAdapter {
    private boolean mIsGrid;
    private ImageURLConverter mUrlConverter;
    private InventoryButtonClickListener mInventoryClickListener;

    public enum RowType {
        CATEGORY_GRID_ROW,
        CATEGORY_LIST_ROW,
        PRODUCT_GRID_ROW,
        PRODUCT_LIST_ROW,
        GRID_LOADING_ROW,
        LIST_LOADING_ROW
    }

    public boolean isGrid() {
        return mIsGrid;
    }

    public void setIsGrid(boolean isGrid) {
        mIsGrid = isGrid;
    }

    private List<IData> mData;
    private NumberFormat mNumberFormat;
    private Context mContext;
    public CategoryProductAdapter(Context context, Integer tenantId, Integer siteId, String siteDomain,InventoryButtonClickListener inventoryClickListener) {
        mUrlConverter = new ImageURLConverter(tenantId, siteId, siteDomain);
        mNumberFormat = NumberFormat.getCurrencyInstance();
        mData = new ArrayList<IData>();
        mInventoryClickListener = inventoryClickListener;
        mContext = context;
    }

    public void setData(List<IData> data) {
        mData = data;
    }

    public void addData(List<IData> data) {
        mData.addAll(data);
    }

    public void setLoadingData(){
        LoadingDataItem dataItem = new LoadingDataItem();
        mData.add(dataItem);
    }

    public void removeLoadingData(){
        mData.remove(mData.size() -1);
    }


    public RowType getRowType(int position) {
        IData dataItem = getItem(position);
        if (dataItem instanceof ProductDataItem) {
            if (isGrid()) {
                return RowType.PRODUCT_GRID_ROW;
            } else {
                return RowType.PRODUCT_LIST_ROW;
            }
        } else if(dataItem instanceof CategoryDataItem){
            if (isGrid()) {
                return RowType.CATEGORY_GRID_ROW;
            } else {
                return RowType.CATEGORY_LIST_ROW;
            }
        } else{
            if (isGrid()){
                return RowType.GRID_LOADING_ROW;
            }else{
                return RowType.LIST_LOADING_ROW;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getRowType(position).ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public IData getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            RowType rowType = getRowType(position);
            if (rowType == RowType.PRODUCT_GRID_ROW) {
                convertView = inflater.inflate(R.layout.product_grid_item, null);
            } else if (rowType == RowType.PRODUCT_LIST_ROW) {
                convertView = inflater.inflate(R.layout.product_list_item, null);
            } else if (rowType == RowType.CATEGORY_GRID_ROW) {
                convertView = inflater.inflate(R.layout.subcategory_grid_item, null);
            } else if (rowType == RowType.CATEGORY_LIST_ROW) {
                convertView = inflater.inflate(R.layout.subcategory_list_item, null);
            } else if( rowType == RowType.GRID_LOADING_ROW){
                convertView = inflater.inflate(R.layout.productcategory_grid_loading, null);
            }else if(rowType == RowType.LIST_LOADING_ROW){
                convertView = inflater.inflate(R.layout.productcategory_list_loading, null);
            }
        }

        bindData(getItem(position), convertView, getRowType(position), mContext);
        return convertView;
    }

    public void bindData(IData data, View convertView, RowType rowType, final Context context) {
        if (rowType == RowType.CATEGORY_GRID_ROW || rowType == RowType.CATEGORY_LIST_ROW) {
            if (!(data instanceof CategoryDataItem)) {
                return;
            }
            CategoryDataItem dataItem = (CategoryDataItem) data;
            Category category = dataItem.getCategory();
            TextView categoryName = (TextView) convertView.findViewById(R.id.category_name);
            final ImageView categoryImage = (ImageView) convertView.findViewById(R.id.category_image);
            final LoadingView categoryLoading = (LoadingView) convertView.findViewById(R.id.category_loading);
            categoryName.setText(category.getContent().getName());
            categoryImage.setImageResource(R.drawable.icon_nocategoryphoto);
            categoryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (category.getContent().getCategoryImages() != null && category.getContent().getCategoryImages().size() > 0) {
                String url = category.getContent().getCategoryImages().get(0).getImageUrl();
                if (!TextUtils.isEmpty(url)) {
                    RequestCreator creator = Picasso.with(context)
                            .load(mUrlConverter.getFullImageUrl(url));

                    if (!isGrid()) {
                        int dimenWidth = context.getResources().getDimensionPixelSize(R.dimen.category_list_item_width);
                        int dimenHeight = context.getResources().getDimensionPixelSize(R.dimen.category_list_item_height);
                        creator = creator.transform(new RoundedTransformation()).placeholder(R.drawable.icon_nocategoryphoto).resize(dimenWidth, dimenHeight).centerCrop();
                        categoryLoading.success();
                    } else {
                        int dimenWidth = context.getResources().getDimensionPixelSize(R.dimen.category_grid_image_width);
                        int dimenHeight = context.getResources().getDimensionPixelSize(R.dimen.category_grid_image_width);
                        creator = creator.placeholder(R.drawable.icon_nocategoryphoto).resize(dimenWidth, dimenHeight).centerInside();
                    }
                    creator.into(categoryImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) categoryImage.getDrawable()).getBitmap();
                            categoryImage.setBackgroundColor(bitmap.getPixel(0, 0));
                            categoryLoading.success();
                        }

                        @Override
                        public void onError() {
                            categoryLoading.success();
                            categoryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        }

                    });
                }
            } else {
                categoryLoading.success();
                categoryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }


        } else if (rowType == RowType.PRODUCT_LIST_ROW || rowType == RowType.PRODUCT_GRID_ROW) {
           TextView productName = (TextView) convertView.findViewById(R.id.product_name);
            final ImageView productImage = (ImageView) convertView.findViewById(R.id.product_image);
           TextView productSku = (TextView) convertView.findViewById(R.id.product_sku);
          TextView productPrice = (TextView) convertView.findViewById(R.id.product_price);
            TextView productSalePrice = (TextView) convertView.findViewById(R.id.product_sale_price);
           ImageView productInventory = (ImageView) convertView.findViewById(R.id.product_image_inventory);
           final LoadingView productLoading = (LoadingView) convertView.findViewById(R.id.product_loading);
            if (!(data instanceof ProductDataItem)) {
                return;
            }

            ProductDataItem dataItem = (ProductDataItem)data;
            final Product product = dataItem.getProduct();
            productName.setText(product.getContent().getProductName());
            productImage.setImageResource(R.drawable.icon_noproductphoto);
            productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //load image asynchronously into the view
            if (product.getContent().getProductImages() != null && product.getContent().getProductImages().size() > 0) {

                RequestCreator creator = Picasso.with(context)
                        .load(mUrlConverter.getFullImageUrl(product.getContent().getProductImages().get(0).getImageUrl()));

                if (!isGrid()) {
                    creator = creator.transform(new RoundedTransformation()).placeholder(R.drawable.icon_noproductphoto);
                    int dimenWidth = context.getResources().getDimensionPixelSize(R.dimen.product_list_item_width);
                    int dimenHeight = context.getResources().getDimensionPixelSize(R.dimen.product_list_item_height);
                    creator = creator.transform(new RoundedTransformation()).placeholder(R.drawable.icon_nocategoryphoto).resize(dimenWidth, dimenHeight).centerCrop();
                } else {
                    int dimenWidth = context.getResources().getDimensionPixelSize(R.dimen.category_grid_image_width);
                    int dimenHeight = context.getResources().getDimensionPixelSize(R.dimen.category_grid_image_height);
                    creator = creator.placeholder(R.drawable.icon_noproductphoto).resize(dimenWidth, dimenHeight).centerInside();
                }

                creator.into(productImage, new Callback() {

                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
                        productImage.setBackgroundColor(bitmap.getPixel(0, 0));
                        productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        productLoading.success();
                    }

                    @Override
                    public void onError() {
                        productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        productLoading.success();
                    }

                });
            }else{
                productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                productLoading.success();
            }

            productSku.setText(product.getProductCode());
            if(product.getPrice() != null && product.getPrice().getSalePrice() != null){
                if(isGrid()) {
                    productPrice.setVisibility(View.GONE);
                }else {
                    productPrice.setVisibility(View.VISIBLE);
                }
                productSalePrice.setVisibility(View.VISIBLE);
            }else{
                productPrice.setVisibility(View.VISIBLE);
                if (isGrid()) {
                    productSalePrice.setVisibility(View.GONE);
                }else {
                    productSalePrice.setVisibility(View.VISIBLE);
                }
            }
            productSalePrice.setText(product.getPrice() != null && product.getPrice().getSalePrice() != null && product.getPrice().getSalePrice() > 0.0 ? mNumberFormat.format(product.getPrice().getSalePrice()) : "");
            productPrice.setText(product.getPrice() != null && product.getPrice().getPrice() != null && product.getPrice().getPrice() > 0.0 ? mNumberFormat.format(product.getPrice().getPrice()) : "");

            productInventory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isGrid()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

        }
    }

}
