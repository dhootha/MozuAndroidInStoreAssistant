package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CategoryViewHolder {

    @InjectView(R.id.category_name) TextView categoryName;
    @InjectView(R.id.category_image) ImageView categoryImage;

    public CategoryViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}