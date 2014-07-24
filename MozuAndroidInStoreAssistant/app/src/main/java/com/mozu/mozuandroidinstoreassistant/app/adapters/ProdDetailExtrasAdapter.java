package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.productadmin.ProductExtra;
import com.mozu.api.contracts.productadmin.ProductExtraValue;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class ProdDetailExtrasAdapter extends ArrayAdapter<ProductExtra> {

    public ProdDetailExtrasAdapter(Context context, List<ProductExtra> extras) {
        super(context, R.layout.prod_detail_extras_item);

        addAll(extras);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.prod_detail_extras_item, parent, false);
        }

        ProductExtra extra = getItem(position);

        TextView value = (TextView) convertView.findViewById(R.id.extra_value);

        if (extra.getValues() != null) {

            value.setText(getExtraValues(extra));
        } else {

            value.setText("N/A");
        }

        return convertView;
    }

    private String getExtraValues(ProductExtra extra) {
        String valueString = "";

        for (ProductExtraValue value: extra.getValues()) {
            valueString += value + ", ";
        }

        if (valueString.length() > 3) {
            valueString = valueString.substring(0, valueString.length() - 3);
        }

        return  valueString;
    }
}
