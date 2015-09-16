package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderInStoreLookupAdapter extends ArrayAdapter<Order> {

    public OrderInStoreLookupAdapter(Context context) {
        super(context, R.layout.order_lookup_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderLookupViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (OrderLookupViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lookup_item, parent, false);
            viewHolder = new OrderLookupViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        Order customer = getItem(position);
        viewHolder.orderLookup.setText(buildCustomerLookupItem(customer));
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
                Order order = (Order) resultValue;
                return order.getId() + " - " + order.getEmail();
            }
        };

    }

    private String buildCustomerLookupItem(Order order) {
        StringBuilder builder = new StringBuilder();
        builder.append(order.getEmail())
                .append(" - (")
                .append(order.getOrderNumber())
                .append(") ");
        return builder.toString();

    }

    class OrderLookupViewHolder {
        @InjectView(R.id.lookup)
        TextView orderLookup;

        public OrderLookupViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
