package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.OrderAttribute;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderAttributeRowItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

import java.util.List;

public class OrderAttributeRowLayout extends LinearLayout implements IRowLayout {
    public OrderAttributeRowLayout(Context context) {
        super(context);
    }

    public OrderAttributeRowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderAttributeRowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        if (data instanceof OrderAttributeRowItem) {
            OrderAttribute orderAttribute = ((OrderAttributeRowItem) data).mOrderAttribute;
            TextView attributeLabel = (TextView) findViewById(R.id.attribute_label);
            TextView attributeValue = (TextView) findViewById(R.id.attribute_value);
            if (TextUtils.isEmpty(orderAttribute.getFullyQualifiedName())) {
                attributeLabel.setText(getResources().getString(R.string.not_available));
            } else {
                attributeLabel.setText(getPropertyValue(orderAttribute.getFullyQualifiedName()));
            }

            String valueStr = getStringValueFromAttributesValues(orderAttribute.getValues());

            if (TextUtils.isEmpty(valueStr)) {
                attributeValue.setText(getResources().getString(R.string.not_available));
            } else {
                attributeValue.setText(valueStr);
            }
        }
    }

    private String getPropertyValue(String fullyQualifiedName) {
        String delimiter = getResources().getString(R.string.attribute_delimiter);
        if (!TextUtils.isEmpty(fullyQualifiedName)) {
            return fullyQualifiedName.substring(fullyQualifiedName.indexOf(delimiter) + 1, fullyQualifiedName.length()).toUpperCase();
        } else {
            return "";
        }
    }


    private String getStringValueFromAttributesValues(List<Object> values) {
        String valueString = "";

        for (Object obj : values) {
            valueString += obj.toString() + " ";
        }

        return valueString;
    }
}


