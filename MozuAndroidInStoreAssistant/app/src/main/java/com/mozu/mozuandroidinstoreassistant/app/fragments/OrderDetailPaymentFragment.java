package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailPaymentsAdapter;

import java.text.NumberFormat;
import java.util.List;


public class OrderDetailPaymentFragment extends Fragment {

    private Order mOrder;

    public OrderDetailPaymentFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_payment_fragment, null);

        if (mOrder != null) {
            setOrderToViews(view);
        }

        return view;
    }

    private void setOrderToViews(View view) {

        ListView paymentList = (ListView) view.findViewById(R.id.payments_list);
        paymentList.setAdapter(new OrderDetailPaymentsAdapter(getActivity(), mOrder.getPayments()));

        TextView orderTotal = (TextView) view.findViewById(R.id.order_total);
        TextView paymentsReceived = (TextView) view.findViewById(R.id.payments_received);
        TextView balance = (TextView) view.findViewById(R.id.balance);

        NumberFormat format = NumberFormat.getInstance();

        orderTotal.setText(format.format(mOrder.getTotal()));
        paymentsReceived.setText(format.format(mOrder.getTotal() - mOrder.getAmountRemainingForPayment()));
        balance.setText(format.format(mOrder.getAmountRemainingForPayment()));
    }


    public void setOrder(Order order) {
        mOrder = order;
    }


}
