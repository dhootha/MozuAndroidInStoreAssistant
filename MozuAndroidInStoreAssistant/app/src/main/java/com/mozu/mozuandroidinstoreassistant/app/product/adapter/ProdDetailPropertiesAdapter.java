package com.mozu.mozuandroidinstoreassistant.app.product.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.ProductProperty;
import com.mozu.api.contracts.productruntime.ProductPropertyValue;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class ProdDetailPropertiesAdapter extends ArrayAdapter<ProductProperty> {

    public ProdDetailPropertiesAdapter(Context context, List<ProductProperty> properties) {
        super(context, R.layout.prod_detail_properties_item);
        if (properties != null) {
            addAll(properties);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.prod_detail_properties_item, parent, false);
        }

        ProductProperty property = getItem(position);

        TextView label = (TextView) convertView.findViewById(R.id.prop_name);
        TextView value = (TextView) convertView.findViewById(R.id.prop_value);

        if (property.getAttributeDetail() != null) {
            label.setText(property.getAttributeDetail().getName());
            String valueText = getPropValues(property);
            if (valueText.isEmpty()) {
                value.setText("N/A");
            } else {
                value.setText(valueText);
            }
        } else {
            label.setText("N/A");
            value.setText("N/A");
        }

        return convertView;
    }

    private String getPropValues(ProductProperty property) {
        String valueString = "";

        for (ProductPropertyValue value : property.getValues()) {
            if (value.getStringValue() != null) {
                valueString += capitalizeFirstLetter(value.getStringValue() + ", ");
            } else if (value.getValue() != null) {
                valueString += capitalizeFirstLetter(value.getValue().toString() + ", ");
            }
        }

        if (valueString.length() > 2) {
            valueString = valueString.substring(0, valueString.length() - 2);
        }

        return valueString;
    }

    private String capitalizeFirstLetter(String inputString){
        return Character.toUpperCase(inputString.charAt(0)) + inputString.substring(1);
    }
}
