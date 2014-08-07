package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.*;
import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailPackageAdapter;


public class OrderDetailFullfillmentFragment extends Fragment {

    public static final String PENDING = "Pending";
    public static final String FULFILLED = "Fulfilled";
    private Order mOrder;

    private TextView mPendingTotal;
    private TextView mFulfilledTotal;
    private TextView mShipmentTotal;

    private ListView mPackageListView;

    public OrderDetailFullfillmentFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_fulfillment_fragment, null);

        mPendingTotal = (TextView) view.findViewById(R.id.shipment_pending_total);
        mFulfilledTotal = (TextView) view.findViewById(R.id.shipment_fulfilled_total);
        mShipmentTotal = (TextView) view.findViewById(R.id.shipment_total);

        mPackageListView = (ListView) view.findViewById(R.id.shipment_list);

        if (mOrder != null) {
            setOrderToViews(view);
        }

        return view;
    }

    private void setOrderToViews(View view) {

        int pendingCount = 0;
        int fulfilledCount = 0;

        for (Package orderPackage: mOrder.getPackages()) {

            String status = orderPackage.getStatus();

            if (status.equalsIgnoreCase(PENDING)) {
                pendingCount++;
            }

            if (status.equalsIgnoreCase(FULFILLED)) {
                fulfilledCount++;
            }

        }

        mPendingTotal.setText(String.valueOf(pendingCount));
        mFulfilledTotal.setText(String.valueOf(fulfilledCount));
        mShipmentTotal.setText(String.valueOf(pendingCount + fulfilledCount));

        mPackageListView.setAdapter(new OrderDetailPackageAdapter(getActivity(), mOrder.getPackages()));
    }


    public void setOrder(Order order) {
        mOrder = order;
    }


}
