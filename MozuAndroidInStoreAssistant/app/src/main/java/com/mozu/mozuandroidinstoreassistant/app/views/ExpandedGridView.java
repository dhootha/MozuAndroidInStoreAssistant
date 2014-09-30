package com.mozu.mozuandroidinstoreassistant.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

import com.mozu.mozuandroidinstoreassistant.app.R;

public class ExpandedGridView extends GridView {

    private android.view.ViewGroup.LayoutParams params;
    private int old_count = 0;

    public ExpandedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getCount() != old_count) {
            old_count = getCount();
            params = getLayoutParams();
            int columnItems = getContext().getResources().getInteger(R.integer.num_of_columns_in_grid);
            int rows = getCount() / columnItems + getCount() % columnItems;

            params.height = rows * (old_count > 0 ? getChildAt(0).getHeight() : 0);
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }
}
