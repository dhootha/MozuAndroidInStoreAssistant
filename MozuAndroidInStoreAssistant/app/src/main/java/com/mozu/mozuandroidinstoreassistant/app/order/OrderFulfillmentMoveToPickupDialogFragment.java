package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentMoveToDataItem;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderFulfillmentMoveToItemAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.PickupObservablesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OrderFulfillmentMoveToPickupDialogFragment extends DialogFragment implements OrderFulfillmentMoveToItemAdapter.MoveToListListener {

    @InjectView(R.id.items)
    RecyclerView mRecyclerViewProducts;
    @InjectView(R.id.cancel)
    Button mCancel;
    @InjectView(R.id.move_to)
    Spinner mMoveToOptions;
    private FulfillmentMoveToDataItem mData;
    private List<String> options;
    private List<Integer> selected = new ArrayList<>();
    private Order mOrder;
    private int createNewPickUpCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fulfillment_move_to, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewProducts.setLayoutManager(layoutManager);
        OrderFulfillmentMoveToItemAdapter adapter = new OrderFulfillmentMoveToItemAdapter(mData.getItems(), this);
        mRecyclerViewProducts.setAdapter(adapter);
        mRecyclerViewProducts.setHasFixedSize(true);
        ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, options);
        dropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMoveToOptions.setAdapter(dropDownAdapter);
        mMoveToOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (selected.size() > 0) {
                        if (position == options.size() - 1) {
                            createNewPickUps();
                        } else {
                            Pickup pickup = mOrder.getPickups().get(position - 1);
                            pickup.getItems().addAll(createNewPickUpItems());
                            AndroidObservable.bindFragment(OrderFulfillmentMoveToPickupDialogFragment.this,
                                    PickupObservablesManager.getInstance(mOrder.getTenantId(), mOrder.getSiteId()).updatePickup(pickup, mOrder.getId()))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(getPickupSubscriber());
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.no_items_selected_error, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }

        });
    }

    public void createNewPickUps() {
        List<Pickup> createPackage = createPickupsByLocale();
        createNewPickUpCount = createPackage.size();
        for (int i = 0; i < createNewPickUpCount; i++) {
            AndroidObservable.bindFragment(OrderFulfillmentMoveToPickupDialogFragment.this,
                    PickupObservablesManager.getInstance(mOrder.getTenantId(), mOrder.getSiteId()).createPickup(createPackage.get(i), mOrder.getId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getPickupSubscriber());
        }
    }

    public void setData(FulfillmentMoveToDataItem data, Order order) {
        this.mData = data;
        this.mOrder = order;
        options = new ArrayList<>(order.getPickups().size() + 2);
        for (Pickup pickup : mOrder.getPickups()) {
            options.add(pickup.getCode());
        }
        options.add("Create New Pickup");
        options.add(0, "Move To:");
    }

    @OnClick(R.id.cancel)
    public void dismissDialog() {
        this.dismiss();
    }

    @Override
    public void onItemSelected(Integer position, boolean isSelected) {
        if (isSelected) {
            selected.add(position);
        } else {
            selected.remove(position);
        }
    }

    private Subscriber<Pickup> getPickupSubscriber() {
        return new Subscriber<Pickup>() {
            @Override
            public void onCompleted() {
                createNewPickUpCount--;
                if (createNewPickUpCount == 0) {
                    OrderFulfillmentMoveToPickupDialogFragment.this.dismiss();
                    ((OrderDetailActivity) getActivity()).onRefresh();
                }
            }

            @Override
            public void onError(Throwable e) {
                ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), e.toString()).show();
            }

            @Override
            public void onNext(Pickup pickup) {
                //Dismiss and reload?
            }
        };
    }

    private List<Pickup> createPickupsByLocale() {
        Map<String, ArrayList<PickupItem>> sortedItems = new HashMap<>();
        List<Pickup> pickups = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            PickupItem item = new PickupItem();
            OrderItem orderItem = mData.getItems().get(selected.get(i));
            String productCode = orderItem.getProduct().getVariationProductCode() != null ? orderItem.getProduct().getVariationProductCode() : orderItem.getProduct().getProductCode();
            item.setProductCode(productCode);
            item.setQuantity(orderItem.getQuantity());
            item.setLineId(orderItem.getLineId());
            if (sortedItems.containsKey(orderItem.getFulfillmentLocationCode())) {
                ArrayList<PickupItem> items1 = sortedItems.get(orderItem.getFulfillmentLocationCode());
                items1.add(item);
                sortedItems.put(orderItem.getFulfillmentLocationCode(), items1);
            } else {
                ArrayList<PickupItem> items1 = new ArrayList<>();
                items1.add(item);
                sortedItems.put(orderItem.getFulfillmentLocationCode(), items1);
            }
        }
        for (Map.Entry<String, ArrayList<PickupItem>> item : sortedItems.entrySet()) {
            Pickup pickup = new Pickup();
            pickup.setItems(item.getValue());
            pickup.setFulfillmentLocationCode(item.getKey());
            pickups.add(pickup);
        }
        return pickups;

    }

    private List<PickupItem> createNewPickUpItems() {
        List<PickupItem> items = new ArrayList<>(selected.size());
        for (int i = 0; i < selected.size(); i++) {
            PickupItem item = new PickupItem();
            OrderItem orderItem = mData.getItems().get(selected.get(i));
            String productCode = orderItem.getProduct().getVariationProductCode() != null ? orderItem.getProduct().getVariationProductCode() : orderItem.getProduct().getProductCode();
            item.setProductCode(productCode);
            item.setQuantity(orderItem.getQuantity());
            item.setLineId(orderItem.getLineId());
            items.add(item);
        }
        return items;
    }
}
