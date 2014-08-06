package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;
import java.util.List;


public class OrderDetailOverviewFragment extends Fragment {

    public static final String N_A = "N/A";
    private Order mOrder;

    private LinearLayout mOrderedItemLayout;

    private NumberFormat mNumberFormat;

    public OrderDetailOverviewFragment() {
        mNumberFormat = NumberFormat.getCurrencyInstance();

        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_overview_fragment, null);

        mOrderedItemLayout = (LinearLayout) view.findViewById(R.id.layout_to_add_ordered_items_to);

        if (mOrder != null) {
            setOrderToViews(view);
        }

        return view;
    }

    private void setOrderToViews(View view) {

        addOrderedItemLayoutsToView(mOrder.getItems());

        TextView total = (TextView) view.findViewById(R.id.order_overview_total);
        total.setText(mNumberFormat.format(mOrder.getTotal()));

    }


    private void addOrderedItemLayoutsToView(List<OrderItem> items) {

        for(OrderItem item: items) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.ordered_item_list_item, mOrderedItemLayout, false);

            TextView code = (TextView) view.findViewById(R.id.ordered_item_code);
            TextView productName = (TextView) view.findViewById(R.id.ordered_item_product);
            TextView price = (TextView) view.findViewById(R.id.ordered_item_price);
            TextView quantity = (TextView) view.findViewById(R.id.ordered_item_quantity);
            TextView total = (TextView) view.findViewById(R.id.ordered_item_total);

            Product product = item.getProduct();

            if (product == null) {
                code.setText(N_A);
                productName.setText(N_A);
                price.setText(N_A);
                quantity.setText(N_A);
                total.setText(N_A);

                mOrderedItemLayout.addView(view);

                continue;
            }

            code.setText(product.getProductCode());
            productName.setText(product.getName());
            price.setText(product.getPrice() != null && product.getPrice().getPrice() != null ? mNumberFormat.format(product.getPrice().getPrice()) : N_A);
            quantity.setText(String.valueOf(item.getQuantity()));
            total.setText(mNumberFormat.format(item.getTotal()));

            mOrderedItemLayout.addView(view);
        }

    }

    public void setOrder(Order order) {
        mOrder = order;
    }


}
