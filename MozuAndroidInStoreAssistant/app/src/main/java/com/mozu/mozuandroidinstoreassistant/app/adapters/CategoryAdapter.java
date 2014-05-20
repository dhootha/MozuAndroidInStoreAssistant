package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.api.contracts.productadmin.Category;
import com.mozu.api.security.Scope;
import com.mozu.mozuandroidinstoreassistant.app.R;

public class CategoryAdapter extends ArrayAdapter<Category> {

    public CategoryAdapter(Context context) {
        super(context, R.layout.category_grid_item);
    }

    @Override
    public long getItemId(int position) {

         return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_grid_item, parent, false);
        }

        Category category = getItem(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.category_name);
        nameTextView.setText(category.getContent().getName());

        return convertView;
    }

}
