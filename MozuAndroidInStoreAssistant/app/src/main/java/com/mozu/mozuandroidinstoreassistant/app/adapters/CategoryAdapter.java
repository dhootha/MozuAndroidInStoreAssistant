package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mozu.api.contracts.productruntime.Category;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends GridToggleArrayAdapter<Category> {

    public CategoryAdapter(Context context) {
        super(context, R.layout.category_grid_item, R.layout.category_list_item);
    }

    @Override
    public long getItemId(int position) {

         return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryViewHolder viewHolder;

        if (convertView == null ||
            (isGrid() && convertView.getId() != getGridResource()) ||
            (!isGrid() && convertView.getId() != getListResource())) {

            convertView = LayoutInflater.from(getContext()).inflate(getCurrentResource(), parent, false);
            viewHolder = new CategoryViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CategoryViewHolder) convertView.getTag();
        }

        Category category = getItem(position);

        viewHolder.categoryName.setText(category.getContent().getName());

        viewHolder.categoryImage.setImageResource(R.drawable.icon_nocategoryphoto);

        //load image asynchronously into the view
        if (category.getContent().getCategoryImages() != null && category.getContent().getCategoryImages().size() > 0) {

            Picasso.with(getContext())
                    .load(category.getContent().getCategoryImages().get(0).getImageUrl())
                    .fit().centerCrop()
                    .into(viewHolder.categoryImage);

        }

        return convertView;
    }

}
