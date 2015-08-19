package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.api.contracts.commerceruntime.orders.ShopperNotes;
import com.mozu.api.contracts.core.AuditInfo;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailNotesAdapter;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class OrderDetailNotesFragment extends Fragment implements OrderNotesUpdateListener {

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
    private OrderDetailNotesAdapter customerNotesAdapter;
    private OrderDetailNotesAdapter internalNotesAdapter;
    private Boolean mIsEditable = false;
    private ListView mNoteList;

    public static OrderDetailNotesFragment getInstance(boolean isEditable) {
        OrderDetailNotesFragment fragment = new OrderDetailNotesFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(NewOrderActivity.IS_EDITABLE, isEditable);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        ((OrderDetailActivity) getActivity()).getEventBusObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getEventSubscriber());

    }

    private Action1<Object> getEventSubscriber() {
        return new Action1<Object>() {

            @Override
            public void call(Object o) {
                if (o instanceof Boolean) {
                    mIsEditable = (Boolean) o;
                    onEditModeUpdateEvent();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mIsEditable = getArguments().getBoolean(NewOrderActivity.IS_EDITABLE, false);
        if (savedInstanceState != null) {
            isCurrentInternalNotes = savedInstanceState.getBoolean(CURRENT_IS_INTERNAL);
        }
        View view = inflater.inflate(R.layout.order_detail_notes_fragment, container, false);

        ButterKnife.inject(this, view);
        mNotesLoadingView = (LoadingView) view.findViewById(R.id.notes_loading_view);
        mCustomerLoadingView = (LoadingView) view.findViewById(R.id.customer_notes_loading_view);

        setOrderToViews(view);

        if (mIsEditable) {
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

        mNoteList = (ListView) view.findViewById(R.id.notes_list);
        internalNotesAdapter = new OrderDetailNotesAdapter(getActivity(), mOrder, true, mIsEditable, this);
        mNoteList.setAdapter(internalNotesAdapter);
        mNoteList.setOnItemClickListener(internalNotesAdapter);
        ListView customerNotesList = (ListView) view.findViewById(R.id.customer_list);
        customerNotesAdapter = new OrderDetailNotesAdapter(getActivity(), mOrder, false, mIsEditable, this);
        customerNotesList.setOnItemClickListener(customerNotesAdapter);
        customerNotesList.setAdapter(customerNotesAdapter);
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

        mAddInternalNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewNoteDialog();
            }
        });

        mAddCustomerNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewNoteDialog();
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

    private void showAddNewNoteDialog() {
        final EditText noteLayout = new EditText(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        noteLayout.setLayoutParams(layoutParams);
        AlertDialog noteDialog = new AlertDialog.Builder(getActivity())
                .setView(noteLayout)
                .setTitle("Add Note")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.add_note, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String note = noteLayout.getText().toString();
                        if (note.isEmpty()) {
                            noteLayout.setError(getActivity().getString(R.string.required));
                            return;
                        }
                        if (isCurrentInternalNotes) {
                            addNewInternalNote(note);
                        } else {
                            addNewCustomerNote(note);
                        }
                        setOrderToViews(OrderDetailNotesFragment.this.getView());
                        dialog.dismiss();
                    }
                })
                .create();
        noteDialog.show();
    }

    private void addNewCustomerNote(String note) {
        ShopperNotes notes = mOrder.getShopperNotes();
        if (notes == null) {
            notes = new ShopperNotes();
        }
        notes.setComments(note);
        mOrder.setShopperNotes(notes);
    }

    private void onEditModeUpdateEvent() {
        if (mIsEditable) {
            mAddCustomerNote.setVisibility(View.VISIBLE);
            mAddInternalNote.setVisibility(View.VISIBLE);
        } else {
            mAddCustomerNote.setVisibility(View.GONE);
            mAddInternalNote.setVisibility(View.GONE);
        }
        customerNotesAdapter.isEditableMode(mIsEditable);
        internalNotesAdapter.isEditableMode(mIsEditable);
    }

    private void addNewInternalNote(String note) {
        List<OrderNote> notes = mOrder.getNotes();
        if (notes == null) {
            notes = new ArrayList<>();
        }

        OrderNote orderNote = new OrderNote();
        AuditInfo auditInfo = new AuditInfo();
        String user = UserAuthenticationStateMachineProducer
                .getInstance(getActivity())
                .getAuthProfile().getUserProfile()
                .getUserId();
        auditInfo.setCreateDate(new DateTime());
        auditInfo.setCreateBy(user);
        orderNote.setAuditInfo(auditInfo);
        orderNote.setText(note);
        notes.add(orderNote);
        mOrder.setNotes(notes);
        customerNotesAdapter.setOrder(mOrder);
        customerNotesAdapter.notifyDataSetChanged();
        FrameLayout.LayoutParams mParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mNoteList.setLayoutParams(mParam);

        ((NewOrderActivity) getActivity()).updateOrder(mOrder);
    }

    @Override
    public void onShopperNotesUpdated(ShopperNotes notes) {
        if (notes == null) {
            mCustomerLoadingView.setError(getActivity().getResources().getString(R.string.not_customer_notes_available));
        }
        this.mOrder.setShopperNotes(notes);
    }

    @Override
    public void onInternalNotesUpdated(List<OrderNote> notes) {
        if (notes == null || notes.isEmpty()) {
            mNotesLoadingView.setError(getActivity().getResources().getString(R.string.not_customer_notes_available));
        }
        this.mOrder.setNotes(notes);

    }
}
