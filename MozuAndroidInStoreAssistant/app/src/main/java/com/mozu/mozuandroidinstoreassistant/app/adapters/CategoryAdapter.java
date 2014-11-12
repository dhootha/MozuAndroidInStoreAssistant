package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mozu.api.contracts.productruntime.Category;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.ImageURLConverter;
import com.mozu.mozuandroidinstoreassistant.app.views.RoundedTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class CategoryAdapter extends GridToggleArrayAdapter<Category> {

    private ImageURLConverter mUrlConverter;

    public CategoryAdapter(Context context, Integer tenantId, Integer siteId,String siteDomain) {
        super(context, R.layout.category_grid_item, R.layout.category_list_item);

        mUrlConverter = new ImageURLConverter(tenantId, siteId,siteDomain);
    }

    @Override
    public long getItemId(int position) {

         return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CategoryViewHolder viewHolder;

        if (convertView == null ||
            (isGrid() && convertView.getId() != getGridResource()) ||
            (!isGrid() && convertView.getId() != getListResource())) {
            convertView = LayoutInflater.from(getContext()).inflate(getCurrentResource(), null);
            viewHolder = new CategoryViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CategoryViewHolder) convertView.getTag();
        }

        Category category = getItem(position);

        viewHolder.categoryName.setText(category.getContent().getName());
        viewHolder.categoryImage.setImageResource(R.drawable.icon_nocategoryphoto);
        viewHolder.categoryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        //load image asynchronously into the view
        if (category.getContent().getCategoryImages() != null && category.getContent().getCategoryImages().size() > 0) {

            String url = category.getContent().getCategoryImages().get(0).getImageUrl();

            if (!TextUtils.isEmpty(url)) {

                RequestCreator creator = Picasso.with(getContext())
                        .load(mUrlConverter.getFullImageUrl(url));

                if (!isGrid()) {
                    int dimenWidth = getContext().getResources().getDimensionPixelSize(R.dimen.category_list_item_width);
                    int dimenHeight = getContext().getResources().getDimensionPixelSize(R.dimen.category_list_item_height);
                    creator = creator.transform(new RoundedTransformation()).placeholder(R.drawable.icon_nocategoryphoto).resize(dimenWidth,dimenHeight).centerCrop();
                    viewHolder.categoryLoading.success();
                } else {
                    int dimenWidth = getContext().getResources().getDimensionPixelSize(R.dimen.category_grid_image_width);
                    int dimenHeight = getContext().getResources().getDimensionPixelSize(R.dimen.category_grid_image_width);
                    creator = creator.placeholder(R.drawable.icon_nocategoryphoto).resize(dimenWidth,dimenHeight).centerInside();
                }
                creator.into(viewHolder.categoryImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) viewHolder.categoryImage.getDrawable()).getBitmap();
                        viewHolder.categoryImage.setBackgroundColor(bitmap.getPixel(0, 0));
                        viewHolder.categoryLoading.success();
                    }

                    @Override
                    public void onError() {
                        viewHolder.categoryLoading.success();
                        viewHolder.categoryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }

                });
            }
        }else{
            viewHolder.categoryLoading.success();
            viewHolder.categoryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        return convertView;
    }

}
