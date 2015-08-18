package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailNotesAdapter;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class OrderDetailNotesFragment extends Fragment {

    @InjectView(R.id.internal_notes_layout)
    LinearLayout mNoteListLayout;
    @InjectView(R.id.customer_notes_layout)
    LinearLayout mCustomerNotesListLayout;
    @InjectView(R.id.show_customer_notes)
    TextView mShowCustomerNotes;
    @InjectView(R.id.show_internal_notes)
    TextView mShowInternalNotes;
    @InjectView(R.id.add_internal_note)
    Button mAddInternalNote;
    @InjectView(R.id.add_customer_note)
    Button mAddCustomerNote;
    private Order mOrder;
    private LoadingView mNotesLoadingView;
    private LoadingView mCustomerLoadingView;
    private boolean isCurrentInternalNotes = false;
    private String CURRENT_IS_INTERNAL = "currentInternal";
    private boolean mIsNewOrder;

    public static OrderDetailNotesFragment getInstance(boolean isNewOrder) {
        OrderDetailNotesFragment fragment = new OrderDetailNotesFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(NewOrderActivity.IS_NEW_ORDER, isNewOrder);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mIsNewOrder = getArguments().getBoolean(NewOrderActivity.IS_NEW_ORDER, false);
        if (savedInstanceState != null) {
            isCurrentInternalNotes = savedInstanceState.getBoolean(CURRENT_IS_INTERNAL);
        }
        View view = inflater.inflate(R.layout.order_detail_notes_fragment, container, false);

        ButterKnife.inject(this, view);
        mNotesLoadingView = (LoadingView) view.findViewById(R.id.notes_loading_view);
        mCustomerLoadingView = (LoadingView) view.findViewById(R.id.customer_notes_loading_view);

        setOrderToViews(view);

        if (mIsNewOrder) {
            mAddCustomerNote.setVisibility(View.VISIBLE);
            mAddInternalNote.setVisibility(View.VISIBLE);
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
        if (mOrder == null || mOrder.getNotes() == null || mOrder.getNotes().size() < 1) {
            mNotesLoadingView.setError(getActivity().getResources().getString(R.string.not_internal_notes_available));
        } else {
            mNotesLoadingView.success();
        }

        if (mOrder == null || mOrder.getShopperNotes() == null || mOrder.getShopperNotes() == null) {
            mCustomerLoadingView.setError(getActivity().getResources().getString(R.string.not_customer_notes_available));
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

        mShowInternalNotes.setOnClickListener(new View.OnClickListener() {
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

    private void showCustomerNotes() {
        mCustomerNotesListLayout.setVisibility(View.VISIBLE);
        mNoteListLayout.setVisibility(View.GONE);
    }

    private void showInternalNotes() {
        mCustomerNotesListLayout.setVisibility(View.GONE);
        mNoteListLayout.setVisibility(View.VISIBLE);
    }

    private void addCustomerNote() {

    }

    private void addInternalNote() {

    }
}
