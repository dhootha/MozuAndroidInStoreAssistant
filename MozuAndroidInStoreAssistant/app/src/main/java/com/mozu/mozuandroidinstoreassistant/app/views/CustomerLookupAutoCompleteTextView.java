package com.mozu.mozuandroidinstoreassistant.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class CustomerLookupAutoCompleteTextView extends AutoCompleteTextView {
    public CustomerLookupAutoCompleteTextView(Context context) {
        super(context);
    }

    public CustomerLookupAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerLookupAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }
}
