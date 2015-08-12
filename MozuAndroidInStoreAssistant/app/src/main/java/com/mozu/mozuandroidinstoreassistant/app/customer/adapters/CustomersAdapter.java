package com.mozu.mozuandroidinstoreassistant.app.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;

public class CustomersAdapter extends ArrayAdapter<CustomerAccount> {

    private NumberFormat mNumberFormat;

    public CustomersAdapter(Context context) {
        super(context, R.layout.customer_list_item);

        mNumberFormat = NumberFormat.getCurrencyInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomerViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (CustomerViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_list_item, parent, false);
            viewHolder = new CustomerViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        CustomerAccount customer = getItem(position);

        viewHolder.customerNumber.setText(String.valueOf(customer.getId()));
        viewHolder.customerLastName.setText(customer.getLastName());
        viewHolder.customerFirstName.setText(customer.getFirstName());
        viewHolder.customerEmail.setText(customer.getEmailAddress());
        if (customer.getCommerceSummary() != null && customer.getCommerceSummary().getTotalOrderAmount() != null && customer.getCommerceSummary().getTotalOrderAmount().getAmount() != null) {
            viewHolder.customerLifetimeValue.setText(mNumberFormat.format(customer.getCommerceSummary().getTotalOrderAmount().getAmount()));
        } else {
            viewHolder.customerLifetimeValue.setText(parent.getResources().getString(R.string.not_available));
        }

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
                CustomerAccount customerAccount = (CustomerAccount) resultValue;
                return customerAccount.getFirstName() + " " + customerAccount.getLastName() + "-" + customerAccount.getEmailAddress();
            }
        };


    }
}
