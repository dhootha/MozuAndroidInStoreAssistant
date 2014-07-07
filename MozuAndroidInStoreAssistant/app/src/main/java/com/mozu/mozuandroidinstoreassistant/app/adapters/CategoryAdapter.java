package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Category;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends ArrayAdapter<Category> {

    private boolean mIsGrid = true;

    public CategoryAdapter(Context context) {
        super(context, R.layout.category_grid_item);
    }

    @Override
    public long getItemId(int position) {

         return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( convertView == null ||
             ( mIsGrid && convertView.findViewById(R.id.category_list_layout) != null) ||
             ( !mIsGrid && convertView.findViewById(R.id.category_grid_layout) != null) ) {

            if (mIsGrid) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_grid_item, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_list_item, parent, false);
            }
        }

        Category category = getItem(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.category_name);
        nameTextView.setText(category.getContent().getName());

        ImageView categoryImageView = (ImageView) convertView.findViewById(R.id.category_image);
        categoryImageView.setImageResource(R.drawable.default_background);

        //load image asynchronously into the view
        if (category.getContent().getCategoryImages() != null && category.getContent().getCategoryImages().size() > 0) {
            Picasso.with(getContext())
                    .load(category.getContent().getCategoryImages().get(0).getImageUrl())
                    .into(categoryImageView);
        }

        return convertView;
    }

    public void setIsGrid(boolean isGrid) {
        mIsGrid = isGrid;
    }

}
