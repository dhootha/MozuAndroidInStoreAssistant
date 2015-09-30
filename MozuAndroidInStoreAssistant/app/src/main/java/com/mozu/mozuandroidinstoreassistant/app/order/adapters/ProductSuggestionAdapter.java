package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ProductSuggestionAdapter extends ArrayAdapter<Product> {

    public ProductSuggestionAdapter(Context context) {
        super(context, R.layout.customer_list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomerLookupViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (CustomerLookupViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_product_list_item, parent, false);
            viewHolder = new CustomerLookupViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        Product product = getItem(position);
        viewHolder.orderProductCode.setText(product.getProductCode());
        viewHolder.orderProductName.setText(product.getContent().getProductName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return "";
            }
        };

    }

    class CustomerLookupViewHolder {
        @InjectView(R.id.order_product_name)
        TextView orderProductName;

        @InjectView(R.id.order_product_code)
        TextView orderProductCode;


        public CustomerLookupViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
