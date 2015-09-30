package com.mozu.mozuandroidinstoreassistant.app.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomerLookupAdapter extends ArrayAdapter<CustomerAccount> {

    public CustomerLookupAdapter(Context context) {
        super(context, R.layout.customer_list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomerLookupViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (CustomerLookupViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_lookup_item, parent, false);
            viewHolder = new CustomerLookupViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        CustomerAccount customer = getItem(position);
        viewHolder.customerLookup.setText(buildCustomerLookupItem(customer));
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

    private String buildCustomerLookupItem(CustomerAccount customer) {
        StringBuilder builder = new StringBuilder();
        builder.append(customer.getLastName())
                .append(",")
                .append(customer.getFirstName())
                .append(" - (")
                .append(customer.getId())
                .append(") ")
                .append(customer.getEmailAddress());
        return builder.toString();

    }

    class CustomerLookupViewHolder {
        @InjectView(R.id.customer_lookup)
        TextView customerLookup;

        public CustomerLookupViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
