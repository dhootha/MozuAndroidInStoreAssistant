package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.api.contracts.core.AuditInfo;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.bus.RxBus;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailNotesAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.OrderNoteObserverable;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.observables.AndroidObservable;
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
        AndroidObservable.bindFragment(this, RxBus.getInstance().toObserverable())
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

        if (mOrder == null || mOrder.getShopperNotes() == null || mOrder.getShopperNotes().getComments() == null) {
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
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_order_notes, null);
        final EditText noteInput = (EditText) view.findViewById(R.id.note);
        final TextView noteTile = (TextView) view.findViewById(R.id.title);
        noteTile.setText(R.string.add_note);
        noteInput.setFocusable(true);
        noteInput.setEnabled(true);
        noteInput.requestFocus();
        noteInput.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        AlertDialog noteDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.new_note, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String note = noteInput.getText().toString();
                        if (note.isEmpty()) {
                            noteInput.setError(getActivity().getString(R.string.required));
                            return;
                        }
                        addNewInternalNote(note);
                        setOrderToViews(OrderDetailNotesFragment.this.getView());
                        dialog.dismiss();
                    }
                })
                .create();
        noteDialog.show();
    }

    private void onEditModeUpdateEvent() {
        if (mIsEditable) {
            mAddInternalNote.setVisibility(View.VISIBLE);
        } else {
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
                .getAuthProfile()
                .getUserProfile()
                .getUserName();
        auditInfo.setCreateDate(new DateTime());
        auditInfo.setCreateBy(user);
        orderNote.setAuditInfo(auditInfo);
        orderNote.setText(note);
        notes.add(orderNote);
        createAndSubscribeToOrderNoteCreation(orderNote);
        mOrder.setNotes(notes);
        internalNotesAdapter.setOrder(mOrder);
        internalNotesAdapter.notifyDataSetChanged();
//todo update order
//        ((NewOrderActivity) getActivity()).updateOrder(mOrder);
    }

    @Override
    public void onInternalNotesUpdated(List<OrderNote> notes, OrderNote updatedNote) {
        createAndSubscribeToOrderNoteUpdate(updatedNote);
        this.mOrder.setNotes(notes);
    }

    @Override
    public void onInternalNoteDeleted(List<OrderNote> notes, OrderNote note) {
        createAndSubscribeToOrderNoteDelete(note);
        if (notes == null || notes.isEmpty()) {
            mNotesLoadingView.setError(getActivity().getResources().getString(R.string.not_customer_notes_available));
        }
    }

    private void createAndSubscribeToOrderNoteUpdate(OrderNote updatedNote) {
        AndroidObservable.bindFragment(this, OrderNoteObserverable.getOrderNoteObserverable(mOrder.getTenantId(), mOrder.getSiteId(), mOrder.getId(), updatedNote, OrderNoteObserverable.OrderCallType.UPDATE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderNote>() {
                    @Override
                    public void call(OrderNote orderNote) {
                        internalNotesAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), "Error Updating Note");
                    }
                });
    }

    private void createAndSubscribeToOrderNoteDelete(OrderNote orderNote) {
        AndroidObservable.bindFragment(this, OrderNoteObserverable.getOrderNoteObserverable(mOrder.getTenantId(), mOrder.getSiteId(), mOrder.getId(), orderNote, OrderNoteObserverable.OrderCallType.DELETION))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderNote>() {
                    @Override
                    public void call(OrderNote orderNote) {
                        internalNotesAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), "Error Deleting Note");
                    }
                });
    }

    private void createAndSubscribeToOrderNoteCreation(OrderNote orderNote) {
        AndroidObservable.bindFragment(this, OrderNoteObserverable.getOrderNoteObserverable(mOrder.getTenantId(), mOrder.getSiteId(), mOrder.getId(), orderNote, OrderNoteObserverable.OrderCallType.CREATION))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderNote>() {
                    @Override
                    public void call(OrderNote orderNote) {
                        internalNotesAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), "Error Deleting Note");
                    }
                });
    }
}
