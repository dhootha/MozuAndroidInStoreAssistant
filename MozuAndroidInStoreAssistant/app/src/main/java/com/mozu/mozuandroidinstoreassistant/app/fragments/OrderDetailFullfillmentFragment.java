package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.*;
import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailFulfillmentPackageAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailPendingFulfillmentAdapter;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailPickupAdapter;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;


public class OrderDetailFullfillmentFragment extends Fragment {

    public static final String PENDING = "Pending";
    public static final String FULFILLED = "Fulfilled";
    private static final String PRODUCT_DIALOG_TAG = "prod_detail_fragment";
    private static final String PACKAGE_DIALOG_TAG = "package_detail_fragment";
    private static final String PICKUP_DIALOG_TAG = "pickup_detail_fragment";
    public static final int FIRST_PICKUP_COUNT = 1;
    private Order mOrder;

    @InjectView(R.id.shipment_pending_total) TextView mPendingTotal;
    @InjectView(R.id.shipment_fulfilled_total) TextView mFulfilledTotal;
    @InjectView(R.id.shipment_total) TextView mShipmentTotal;
    @InjectView(R.id.pickup_total) TextView mPickupTotal;
    @InjectView(R.id.pickup_pending_total) TextView mPickupPending;
    @InjectView(R.id.pickup_pickedup_total) TextView mPickedUp;

    @InjectView(R.id.pickup_layout) LinearLayout mPickupLabels;
    @InjectView(R.id.ship_layout) LinearLayout mShipLabels;

    @InjectView(R.id.shipment_pending_list) ListView mShippingPendingListView;
    @InjectViews({R.id.pending_shipment_items_layout, R.id.shipment_pending_list}) List<View> mShippingPendingViews;

    @InjectView(R.id.shipment_not_fulfilled_list) ListView mShippingNotFulfilledListView;
    @InjectView(R.id.not_fulfilled_divider) View mNotFulfilledDivider;
    @InjectViews({R.id.not_fulfilled_shipment_items_layout, R.id.shipment_not_fulfilled_list}) List<View> mShippingNotFulfilledViews;

    @InjectView(R.id.shipment_fulfilled_list) ListView mShippingFulfilledListView;
    @InjectView(R.id.fulfilled_divider) View mFulfilledDivider;
    @InjectViews({R.id.fulfilled_shipment_items_layout, R.id.shipment_fulfilled_list}) List<View> mShippingFulfilledViews;

    @InjectView(R.id.pickup_pickedup_list) ListView mPickedupList;
    @InjectView(R.id.pickedup_divider) View mPickedUpDivider;
    @InjectViews({R.id.pickedup_items_layout, R.id.pickup_pickedup_list}) List<View> mPickedupViews;

    @InjectView(R.id.pickup_pending_list) ListView mNotPickedupList;
    @InjectViews({R.id.not_pickedup_items_layout, R.id.pickup_pending_list}) List<View> mNotPickedupViews;

    @InjectView(R.id.pickup_detail_layout) LinearLayout mPickupDetailLayout;
    @InjectView(R.id.shipment_detail_layout) LinearLayout mShipmentDetailLayout;

    public OrderDetailFullfillmentFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_fulfillment_fragment, null);

        ButterKnife.inject(this, view);

        if (mOrder != null) {
            setOrderToViews();
        }

        return view;
    }

    private void setOrderToViews() {

        setShipmentLayouts();
        setPickupsLayout();

    }

    private void setPickupsLayout() {
        //if no pickups, then hide pickups and move along
        if (mOrder.getPickups() == null || mOrder.getPickups().size() < 1) {
            mPickupLabels.setVisibility(View.GONE);

            mPickupDetailLayout.setVisibility(View.GONE);

            ButterKnife.apply(mNotPickedupViews, GONE);
            ButterKnife.apply(mPickedupViews, GONE);

            return;
        }

        boolean isNotPickedupShowing;

        List<Pickup> unPickedUps = new ArrayList<Pickup>();
        List<Pickup> pickedUps = new ArrayList<Pickup>();

        mPickupTotal.setText(String.valueOf(mOrder.getPickups().size()));

        for (Pickup pickup: mOrder.getPickups()) {
            if (pickup.getStatus().equalsIgnoreCase(FULFILLED)) {

                pickedUps.add(pickup);
            } else {

                unPickedUps.add(pickup);
            }
        }

        mPickupPending.setText(String.valueOf(unPickedUps.size()));
        mPickedUp.setText(String.valueOf(pickedUps.size()));
        mPickupTotal.setText(String.valueOf(unPickedUps.size() + pickedUps.size()));

        //setup un pickedup pickups
        if (unPickedUps.size() < 1) {
            ButterKnife.apply(mNotPickedupViews, GONE);
            isNotPickedupShowing = false;
        } else {
            ButterKnife.apply(mNotPickedupViews, VISIBLE);

            mNotPickedupList.setAdapter(new OrderDetailPickupAdapter(getActivity(), unPickedUps, FIRST_PICKUP_COUNT));
            mNotPickedupList.setOnItemClickListener(mPickupItemClickListener);

            isNotPickedupShowing = true;
        }

        //setup un pickedup pickups
        if (pickedUps.size() < 1) {
            ButterKnife.apply(mPickedupViews, GONE);
        } else {
            ButterKnife.apply(mPickedupViews, VISIBLE);

            mPickedUpDivider.setVisibility(isNotPickedupShowing ? View.VISIBLE : View.GONE);

            mPickedupList.setAdapter(new OrderDetailPickupAdapter(getActivity(), pickedUps, unPickedUps.size() + FIRST_PICKUP_COUNT));
            mPickedupList.setOnItemClickListener(mPickupItemClickListener);
        }
    }

    private void setShipmentLayouts() {
        boolean isPendingVisible;
        boolean isNotFulfilledVisible;

        int pendingCount = 0;
        int fulfilledCount = 0;

        List<FulfillmentItem> fulfillmentItemList = new ArrayList<FulfillmentItem>();

        List<String> alreadyPackagedItems = new ArrayList<String>();

        int packageCount = 0;

        for (Package orderPackage: mOrder.getPackages()) {
            packageCount++;

            FulfillmentItem fulfillmentItem = new FulfillmentItem();
            fulfillmentItem.setPackaged(true);
            fulfillmentItem.setOrderPackage(orderPackage);

            String status = orderPackage.getStatus();

            for (PackageItem packageItem: orderPackage.getItems()) {
                alreadyPackagedItems.add(packageItem.getProductCode());
            }

            if (status.equalsIgnoreCase(PENDING)) {
                pendingCount++;

                fulfillmentItem.setFullfilled(false);
            }

            if (status.equalsIgnoreCase(FULFILLED)) {
                fulfilledCount++;

                fulfillmentItem.setFullfilled(true);

                for (Shipment shipment: mOrder.getShipments()) {
                    if (shipment.getId().equalsIgnoreCase(orderPackage.getShipmentId())) {
                        fulfillmentItem.setShipment(shipment);

                        break;
                    }
                }
            }

            fulfillmentItem.setPackageNumber(getActivity().getString(R.string.package_number_string) + String.valueOf(packageCount));

            fulfillmentItemList.add(fulfillmentItem);
        }

        //find all the items that have not been packaged yet
        List<OrderItem> orderItemsNotPackaged = new ArrayList<OrderItem>(mOrder.getItems());

        for (String productCode: alreadyPackagedItems) {

            for (int i = 0; i < orderItemsNotPackaged.size(); i++) {
                OrderItem orderItem = orderItemsNotPackaged.get(i);

                if (productCode.equalsIgnoreCase(orderItem.getProduct().getProductCode())) {

                    orderItemsNotPackaged.remove(i);
                    i--;
                }

            }
        }

        //set header info for shipments, a pending package according to the
        //documentation is one that has not shipped
        mPendingTotal.setText(String.valueOf(pendingCount + orderItemsNotPackaged.size()));
        mFulfilledTotal.setText(String.valueOf(fulfilledCount));
        mShipmentTotal.setText(String.valueOf(pendingCount + orderItemsNotPackaged.size() + fulfilledCount));

        //if there are no fulfillment items or unpackaged items, then hide the ship labels
        if ((fulfillmentItemList == null || fulfillmentItemList.size() < 1) && (orderItemsNotPackaged == null || orderItemsNotPackaged.size() < 1) ) {
            mShipLabels.setVisibility(View.GONE);
            mShipmentDetailLayout.setVisibility(View.GONE);

            return;
        }

        //show the pending items if possible
        if (orderItemsNotPackaged.size() < 1) {
            ButterKnife.apply(mShippingPendingViews, GONE);
            isPendingVisible = false;
        } else {
            ButterKnife.apply(mShippingPendingViews, VISIBLE);


            mShippingPendingListView.setAdapter(new OrderDetailPendingFulfillmentAdapter(getActivity(), orderItemsNotPackaged));
            mShippingPendingListView.setOnItemClickListener(mDirectShipPendingItemClickListener);
            isPendingVisible = true;
        }

        //show not fulfilled items if possible
        if (pendingCount < 1) {
            ButterKnife.apply(mShippingNotFulfilledViews, GONE);
            isNotFulfilledVisible = false;
        } else {
            ButterKnife.apply(mShippingNotFulfilledViews, VISIBLE);

            mNotFulfilledDivider.setVisibility(isPendingVisible ? View.VISIBLE : View.GONE);

            List<FulfillmentItem> notFulfilledItems = new ArrayList<FulfillmentItem>();

            for (FulfillmentItem item: fulfillmentItemList) {
                if (!item.isFullfilled()) {
                    notFulfilledItems.add(item);
                }
            }

            mShippingNotFulfilledListView.setAdapter(new OrderDetailFulfillmentPackageAdapter(getActivity(), notFulfilledItems));
            mShippingNotFulfilledListView.setOnItemClickListener(mDirectShipPackageClickListener);

            isNotFulfilledVisible = true;
        }

        //show fulfilled items if possible
        if (fulfilledCount < 1) {
            ButterKnife.apply(mShippingFulfilledViews, GONE);
        } else {
            ButterKnife.apply(mShippingFulfilledViews, VISIBLE);

            mFulfilledDivider.setVisibility(isPendingVisible || isNotFulfilledVisible ? View.VISIBLE : View.GONE);

            List<FulfillmentItem> fulfilledItems = new ArrayList<FulfillmentItem>();

            for (FulfillmentItem item: fulfillmentItemList) {
                if (item.isFullfilled()) {
                    fulfilledItems.add(item);
                }
            }

            mShippingFulfilledListView.setAdapter(new OrderDetailFulfillmentPackageAdapter(getActivity(), fulfilledItems));
            mShippingFulfilledListView.setOnItemClickListener(mDirectShipPackageClickListener);
        }
    }


    public void setOrder(Order order) {

        mOrder = order;
    }

    private AdapterView.OnItemClickListener mDirectShipPackageClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            FulfillmentItem item = (FulfillmentItem) parent.getItemAtPosition(position);

            showPackageDetail(item);

        }

    };

    private AdapterView.OnItemClickListener mDirectShipPendingItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            OrderItem item = (OrderItem) parent.getItemAtPosition(position);

            showProductDetailDialog(item);

        }

    };

    private AdapterView.OnItemClickListener mPickupItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Pickup item = (Pickup) parent.getItemAtPosition(position);

            showPickupDetailDialog(item);

        }

    };

    private void showProductDetailDialog(OrderItem item) {
        FragmentManager manager = getFragmentManager();
        ProductDetailOverviewDialogFragment productOverviewFragment = (ProductDetailOverviewDialogFragment) manager.findFragmentByTag(PRODUCT_DIALOG_TAG);

        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        if (productOverviewFragment == null) {
            productOverviewFragment = new ProductDetailOverviewDialogFragment();
            productOverviewFragment.setProduct(item.getProduct());
            productOverviewFragment.setTenantId(userState.getTenantId());
            productOverviewFragment.setSiteId(userState.getSiteId());
        }

        productOverviewFragment.show(manager, PRODUCT_DIALOG_TAG);
    }

    private void showPackageDetail(FulfillmentItem item) {
        FragmentManager manager = getFragmentManager();
        PackageInfoDialogFragment packageInfoDialogFragment = (PackageInfoDialogFragment) manager.findFragmentByTag(PACKAGE_DIALOG_TAG);

        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        if (packageInfoDialogFragment == null) {
            packageInfoDialogFragment = new PackageInfoDialogFragment();
            packageInfoDialogFragment.setFulfillmentItem(item);
            packageInfoDialogFragment.setTenantAndSiteId(userState.getTenantId(), userState.getSiteId());
        }

        packageInfoDialogFragment.show(manager, PACKAGE_DIALOG_TAG);
    }

    private void showPickupDetailDialog(Pickup item) {
        FragmentManager manager = getFragmentManager();
        PickupInfoDialogFragment pickupInfoDialogFragment = (PickupInfoDialogFragment) manager.findFragmentByTag(PICKUP_DIALOG_TAG);

        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

        if (pickupInfoDialogFragment == null) {
            pickupInfoDialogFragment = new PickupInfoDialogFragment();
            pickupInfoDialogFragment.setPickup(item);
            pickupInfoDialogFragment.setTenantAndSiteId(userState.getTenantId(), userState.getSiteId());
        }

        pickupInfoDialogFragment.show(manager, PICKUP_DIALOG_TAG);
    }

    static final ButterKnife.Action<View> GONE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setVisibility(View.GONE);
        }
    };

    static final ButterKnife.Action<View> VISIBLE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setVisibility(View.VISIBLE);
        }
    };
}
