package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailNotesAdapter;


public class OrderDetailNotesFragment extends Fragment {

    private Order mOrder;

    public OrderDetailNotesFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_notes_fragment, null);

        if (mOrder != null) {
            setOrderToViews(view);
        }

        return view;
    }

    private void setOrderToViews(View view) {

        ListView noteList = (ListView) view.findViewById(R.id.notes_list);

        noteList.setAdapter(new OrderDetailNotesAdapter(getActivity(), mOrder.getNotes()));

    }


    public void setOrder(Order order) {
        mOrder = order;
    }


}
