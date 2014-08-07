package com.mozu.mozuandroidinstoreassistant.app.views;

import android.app.Fragment;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class HeightWrappingViewPager extends ViewPager {

    public HeightWrappingViewPager(Context context) {
        super(context);
    }

    public HeightWrappingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super has to be called in the beginning so the child views can be
        // initialized.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() <= 0)
            return;

        // Check if the selected layout_height mode is set to wrap_content
        // (represented by the AT_MOST constraint).
        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;

        int width = getMeasuredWidth();

        int childCount = getChildCount();

        int height = getChildAt(0).getMeasuredHeight();
        int fragmentHeight = 0;

        for (int index = 0; index < childCount; index++) {
            View firstChild = getChildAt(index);

            // Initially set the height to that of the first child - the
            // PagerTitleStrip (since we always know that it won't be 0).
            height = firstChild.getMeasuredHeight() > height ? firstChild.getMeasuredHeight() : height;

            int fHeight = measureFragment(((Fragment) getAdapter().instantiateItem(this, index)).getView());

            fragmentHeight = fHeight > fragmentHeight ? fHeight : fragmentHeight;

        }

        if (wrapHeight) {

            // Keep the current measured width.
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

        }

        // Just add the height of the fragment:
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + fragmentHeight, MeasureSpec.EXACTLY);

        // super has to be called again so the new specs are treated as
        // exact measurements.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int measureFragment(View view) {
        if (view == null)
            return 0;

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }
}