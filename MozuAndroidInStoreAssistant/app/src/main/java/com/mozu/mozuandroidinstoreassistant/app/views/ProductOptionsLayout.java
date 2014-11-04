package com.mozu.mozuandroidinstoreassistant.app.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.ProductOptionValue;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class ProductOptionsLayout extends LinearLayout {

    private TextView mTitle;
    private Spinner mSpinner;
    public ProductOptionsLayout(Context context) {
        super(context);
        initViews(context);

    }

    private void initViews(Context context){
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_option, this, true);
        mTitle = (TextView) getChildAt(0);
        mSpinner = (Spinner)getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed,left,top,right,bottom);
        MarginLayoutParams margins = MarginLayoutParams.class.cast(getLayoutParams());
        margins.setMargins(0,0,20,0);
        setLayoutParams(margins);
    };

    public void setTitle(String title){
        mTitle.setText(title);
    }

    public void setSpinnerOptions(List<ProductOptionValue> productOptions){
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getContext(),R.layout.productoption_spinner_item,R.id.product_option_name,productOptions);
        if (productOptions.size() > 1) {
            mSpinner.setClickable(true);
        } else {
            mSpinner.setClickable(false);
        }
        mSpinner.setAdapter(spinnerAdapter);
    }

    class SpinnerAdapter extends ArrayAdapter<ProductOptionValue> {
        public SpinnerAdapter(Context context,int res,int textViewResourceId, List<ProductOptionValue> objects) {
            super(context, res,textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.productoption_spinner_item, parent, false);
            }
            TextView mTextView = (TextView) convertView.findViewById(R.id.product_option_name);
            ProductOptionValue optionValue = getItem(position);
            mTextView.setText(optionValue.getStringValue());
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(parent.getContext(), R.layout.productoption_dropdown_resource, null);
            textView.setText(getItem(position).getStringValue());
            return textView;

        }

    }
}
