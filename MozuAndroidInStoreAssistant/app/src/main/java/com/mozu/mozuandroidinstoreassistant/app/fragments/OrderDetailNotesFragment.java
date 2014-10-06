package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailNotesAdapter;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class OrderDetailNotesFragment extends Fragment {

    private Order mOrder;

    @InjectView(R.id.internal_notes_layout) LinearLayout mNoteListLayout;
    @InjectView(R.id.customer_notes_layout) LinearLayout mCustomerNotesListLayout;
    @InjectView(R.id.show_customer_notes) TextView mShowCustomerNotes;
    @InjectView(R.id.show_internal_notes) TextView mShowInternalNotes;

private LoadingView mNotesLoadingView;
    private LoadingView mCustomerLoadingView;
    private boolean isCurrentInternalNotes = false;
    private String CURRENT_IS_INTERNAL = "currentInternal";

    public OrderDetailNotesFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState != null){
            isCurrentInternalNotes = savedInstanceState.getBoolean(CURRENT_IS_INTERNAL);
        }
        View view = inflater.inflate(R.layout.order_detail_notes_fragment, null);

        ButterKnife.inject(this, view);
         mNotesLoadingView = (LoadingView) view.findViewById(R.id.notes_loading_view);
         mCustomerLoadingView = (LoadingView) view.findViewById(R.id.customer_notes_loading_view);

        if (mOrder != null) {
            setOrderToViews(view);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CURRENT_IS_INTERNAL, isCurrentInternalNotes);
        super.onSaveInstanceState(outState);
    }

    private void setOrderToViews(View view) {

        ListView noteList = (ListView) view.findViewById(R.id.notes_list);
        noteList.setAdapter(new OrderDetailNotesAdapter(mOrder, true));
        ListView customerNotesList = (ListView) view.findViewById(R.id.customer_list);
        customerNotesList.setAdapter(new OrderDetailNotesAdapter(mOrder, false));
        if (mOrder == null || mOrder.getNotes().size() < 1) {
            mNotesLoadingView.setError("No Notes Available");
        } else {
            mNotesLoadingView.success();
        }

        if (mOrder == null || mOrder.getShopperNotes() == null) {
            mCustomerLoadingView.setError("No Notes Available");
        } else {
            mCustomerLoadingView.success();
        }
        if (isCurrentInternalNotes) {
            showInternalNotes();
        } else {
            showCustomerNotes();
        }
        mShowCustomerNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomerNotes();
                isCurrentInternalNotes = false;
            }
        });

        mShowInternalNotes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showInternalNotes();
                isCurrentInternalNotes = true;
            }
        });

    }

    public void setOrder(Order order) {
        mOrder = order;
    }

    public void showCustomerNotes(){
        mCustomerNotesListLayout.setVisibility(View.VISIBLE);
        mNoteListLayout.setVisibility(View.GONE);
    }

    public void showInternalNotes(){
        mCustomerNotesListLayout.setVisibility(View.GONE);
        mNoteListLayout.setVisibility(View.VISIBLE);
    }
}
