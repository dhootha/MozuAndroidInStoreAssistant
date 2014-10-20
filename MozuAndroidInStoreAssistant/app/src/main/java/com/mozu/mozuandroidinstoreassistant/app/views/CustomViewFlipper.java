package com.mozu.mozuandroidinstoreassistant.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * http://stackoverflow.com/questions/8050730/viewflipper-receiver-not-registered
 */

public class CustomViewFlipper extends ViewFlipper {
    public CustomViewFlipper(Context context) {
        super(context);
    }
    public CustomViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        }
        catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }
}
