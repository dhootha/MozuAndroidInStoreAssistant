package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.api.contracts.commerceruntime.orders.ShopperNotes;
import com.mozu.api.contracts.core.AuditInfo;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailNotesAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.OrderNoteObserverable;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import org.joda.time.DateTime;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class OrderDetailNotesFragment extends Fragment {

    @InjectView(R.id.internal_notes_layout)
    LinearLayout mNoteListLayout;
    @InjectView(R.id.customer_notes_layout)
    LinearLayout mCustomerNotesLayout;
    @InjectView(R.id.show_customer_notes)
    TextView mShowCustomerNotes;
    @InjectView(R.id.show_internal_notes)
    TextView mShowInternalNotes;
    @InjectView(R.id.add_internal_note)
    Button mAddInternalNote;


    @InjectView(R.id.customer_notes)
    TextView mCustomerNote;

    @InjectView(R.id.save_customer_note)
    Button mCustomerNoteSave;


    private Order mOrder;
    private LoadingView mNotesLoadingView;
    private boolean isCurrentInternalNotes = false;
    private String CURRENT_IS_INTERNAL = "currentInternal";
    private OrderDetailNotesAdapter internalNotesAdapter;
    private ListView mNoteList;

    public static OrderDetailNotesFragment getInstance() {
        OrderDetailNotesFragment fragment = new OrderDetailNotesFragment();
        Bundle bundle = new Bundle();
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
        if (savedInstanceState != null) {
            isCurrentInternalNotes = savedInstanceState.getBoolean(CURRENT_IS_INTERNAL);
        }
        View view = inflater.inflate(R.layout.order_detail_notes_fragment, container, false);
        ButterKnife.inject(this, view);
        mNotesLoadingView = (LoadingView) view.findViewById(R.id.notes_loading_view);
        setOrderToViews(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CURRENT_IS_INTERNAL, isCurrentInternalNotes);
        super.onSaveInstanceState(outState);
    }

    private void setOrderToViews(View view) {
        mNoteList = (ListView) view.findViewById(R.id.notes_list);
        internalNotesAdapter = new OrderDetailNotesAdapter(mOrder.getNotes());
        mNoteList.setAdapter(internalNotesAdapter);
        mNoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrderNote orderNote = (OrderNote) adapterView.getItemAtPosition(i);
                showEditNoteDialog(orderNote);
            }
        });
        setCustomerNotes();
        if (mOrder == null || mOrder.getNotes() == null || mOrder.getNotes().size() < 1) {
            mNotesLoadingView.setError(getActivity().getResources().getString(R.string.not_internal_notes_available));
        } else {
            mNotesLoadingView.success();
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

    private void showEditNoteDialog(final OrderNote orderNote) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_edit_order_notes, null);
        final EditText editText = (EditText) view.findViewById(R.id.note);
        final TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(R.string.edit_note);
        editText.setText(orderNote.getText());
        final AlertDialog editNoteDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.delete, null)
                .setNeutralButton(R.string.edit, null)
                .setPositiveButton(R.string.done, null)
                .create();
        editNoteDialog.show();
        Button positive = editNoteDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button neutral = editNoteDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        Button negative = editNoteDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem(editText.getText().toString(), orderNote);
                editNoteDialog.dismiss();
            }
        });
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setEnabled(true);
                editText.setFocusable(true);
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(orderNote);
                editNoteDialog.dismiss();
            }
        });
    }


    public void setOrder(Order order) {
        mOrder = order;
    }

    private void showCustomerNotes() {
        mCustomerNotesLayout.setVisibility(View.VISIBLE);
        mNoteListLayout.setVisibility(View.GONE);
    }

    private void showInternalNotes() {
        mCustomerNotesLayout.setVisibility(View.GONE);
        mNoteListLayout.setVisibility(View.VISIBLE);
    }

    private void updateItem(String latestNote, OrderNote orderNote) {
        onInternalNotesUpdated(latestNote, orderNote);
    }

    private void deleteItem(OrderNote orderNote) {
        onInternalNoteDeleted(orderNote);
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

    private void setCustomerNotes() {
        if (mOrder.getShopperNotes() == null || mOrder.getShopperNotes().getComments() == null) {
            if (mOrder.getStatus().equalsIgnoreCase("Pending")) {
                mCustomerNote.setHint("Add Customer Notes Here");
            } else {
                mCustomerNote.setText("No Customer Notes Available");
            }
        } else {
            mCustomerNote.setText(mOrder.getShopperNotes().getComments());
        }

        if (mOrder.getStatus().equalsIgnoreCase("Pending")) {
            mCustomerNoteSave.setVisibility(View.VISIBLE);
            mCustomerNoteSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEditCustomerNote(mCustomerNote.getText().toString());
                }
            });
        } else {
            mCustomerNoteSave.setVisibility(View.GONE);
        }


    }

    private void addNewInternalNote(String note) {
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
        createAndSubscribeToOrderNoteCreation(orderNote);
    }

    private void addEditCustomerNote(String note) {
        ShopperNotes shopperNotes = new ShopperNotes();
        shopperNotes.setComments(note);
        updateCustomerNotes(mOrder, shopperNotes);
    }

    private void updateCustomerNotes(final Order order, ShopperNotes shopperNotes) {
        final ShopperNotes currentShopperNotes = order.getShopperNotes();
        order.setShopperNotes(shopperNotes);
        AndroidObservable.bindFragment(this, NewOrderManager.getInstance().getUpdateOrderObservable(order.getTenantId(), order.getSiteId(), order, order.getId())).subscribe(new Subscriber<Order>() {
            @Override
            public void onCompleted() {
                Toast.makeText(getActivity(), "Customer Notes Saved Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                mOrder.setShopperNotes(currentShopperNotes);
                ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), "Error Updating Customer Note");
            }

            @Override
            public void onNext(Order updatedOrder) {
                mOrder = updatedOrder;
            }
        });
    }

    public void onInternalNotesUpdated(String note, OrderNote currentNote) {
        OrderNote updatedNote = new OrderNote();
        updatedNote.setText(note);
        updatedNote.setId(currentNote.getId());
        createAndSubscribeToOrderNoteUpdate(updatedNote);
    }


    public void onInternalNoteDeleted(OrderNote note) {
        createAndSubscribeToOrderNoteDelete(note);
    }

    private void createAndSubscribeToOrderNoteUpdate(OrderNote updatedNote) {
        AndroidObservable.bindFragment(this, OrderNoteObserverable.getOrderNoteObserverable(mOrder.getTenantId(), mOrder.getSiteId(), mOrder.getId(), updatedNote, OrderNoteObserverable.OrderCallType.UPDATE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderNote>() {
                    @Override
                    public void call(OrderNote orderNote) {
                        for (OrderNote note : mOrder.getNotes()) {
                            if (note.getId().equalsIgnoreCase(orderNote.getId())) {
                                note.setText(orderNote.getText());
                                note.setAuditInfo(orderNote.getAuditInfo());
                            }
                        }
                        internalNotesAdapter.setData(mOrder.getNotes());
                        internalNotesAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), "Error Updating Note");
                    }
                });
    }

    private void createAndSubscribeToOrderNoteDelete(final OrderNote orderNote) {
        AndroidObservable.bindFragment(this, OrderNoteObserverable.getOrderNoteObserverable(mOrder.getTenantId(), mOrder.getSiteId(), mOrder.getId(), orderNote, OrderNoteObserverable.OrderCallType.DELETION))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OrderNote>() {
                    @Override
                    public void call(OrderNote updatedOrderNote) {
                        mOrder.getNotes().remove(orderNote);
                        internalNotesAdapter.setData(mOrder.getNotes());
                        internalNotesAdapter.notifyDataSetChanged();
                        if (mOrder.getNotes().size() < 1) {
                            mNotesLoadingView.setError(getActivity().getResources().getString(R.string.not_internal_notes_available));
                        }
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
                        mOrder.getNotes().add(orderNote);
                        internalNotesAdapter.setData(mOrder.getNotes());
                        internalNotesAdapter.notifyDataSetChanged();
                        mNotesLoadingView.success();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), "Error Deleting Note");
                    }
                });
    }
}
