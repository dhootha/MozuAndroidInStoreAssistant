package com.mozu.mozuandroidinstoreassistant.app.layout.customer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerAccountAttribute;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class CustomerAttributeLayout extends LinearLayout implements IRowLayout {
    public CustomerAttributeLayout(Context context) {
        super(context);
    }

    public CustomerAttributeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerAttributeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView attributeProperty = (TextView) findViewById(R.id.attribute_label);
        TextView attributeValue = (TextView) findViewById(R.id.attribute_value);
        if (data instanceof CustomerAccountAttribute) {
            CustomerAccountAttribute customerAccountAttribute = (CustomerAccountAttribute) data;
            attributeValue.setText(customerAccountAttribute.getValue());
            attributeProperty.setText(customerAccountAttribute.getProperty());
        } else {
            attributeValue.setText(getResources().getString(R.string.not_available));
        }

    }
}
