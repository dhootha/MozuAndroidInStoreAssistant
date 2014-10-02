package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.fulfillment.PackageItem;
import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.api.contracts.commerceruntime.fulfillment.Shipment;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.BottomRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentHeaderDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentPackageDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentPickupItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.TopRowItem;
import com.mozu.mozuandroidinstoreassistant.app.fragments.PackageInfoDialogFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.PickupInfoDialogFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.ProductDetailOverviewDialogFragment;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class OrderDetailFullfillmentFragment extends Fragment {

    public static final String PENDING = "Pending";
    public static final String FULFILLED = "Fulfilled";
    public static final String NOTFULLFILLED = "NotFulfilled";

    public static final String SHIP = "Ship";
    public static final String PICKUP = "pickup";
    private static final String PRODUCT_DIALOG_TAG = "prod_detail_fragment";
    private static final String PACKAGE_DIALOG_TAG = "package_detail_fragment";
    private static final String PICKUP_DIALOG_TAG = "pickup_detail_fragment";
    public static final int FIRST_PICKUP_COUNT = 1;
    private Order mOrder;

    private ListView mFullfillmentListview;
    private OrderDetailFullfillmentAdapter mOrderDetailFullfillmentAdapter;

    List<OrderItem> mShipItems = new ArrayList<OrderItem>();
    List<OrderItem> mPickupItems = new ArrayList<OrderItem>();
    private TextView mOrderStatusTextView;

    public OrderDetailFullfillmentFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_fulfillment_layout, null);
        mFullfillmentListview = (ListView) view.findViewById(R.id.fullfillment_list);
        mOrderStatusTextView = (TextView)view.findViewById(R.id.order_fulfillment_status);
        if (mOrder != null) {
            categorizeOrders(mOrder);
            mOrderStatusTextView.setText(mOrder.getFulfillmentStatus());
            List<IData> data = filterShipment(mShipItems);
            data.addAll(filterPickUp(mPickupItems));
            mOrderDetailFullfillmentAdapter = new OrderDetailFullfillmentAdapter(getActivity(), data);
            mFullfillmentListview.setAdapter(mOrderDetailFullfillmentAdapter);
            mFullfillmentListview.setOnItemClickListener(mListClickListener);
        }
        return view;
    }


    private void categorizeOrders(Order order) {
        if (order == null || order.getItems() == null || order.getItems().size() < 1)
            return;
        for (OrderItem item : order.getItems()) {
            if (SHIP.equalsIgnoreCase(item.getFulfillmentMethod())) {
                mShipItems.add(item);
            } else if (PICKUP.equalsIgnoreCase(item.getFulfillmentMethod())) {
                mPickupItems.add(item);
            }
        }
    }

    private List<OrderItem> removeOrderItem(List<OrderItem> orderItems, String productCode) {
        Iterator<OrderItem> i = orderItems.iterator();
        while (i.hasNext()) {
            OrderItem item = i.next();
            if (productCode.toLowerCase().startsWith(item.getProduct().getProductCode().toLowerCase())) {
                i.remove();
            }
        }

        return orderItems;
    }


    public void setOrder(Order order) {
        mOrder = order;
    }

    private List<IData> filterPickUp(List<OrderItem> pickupItems) {
        List<IData> finalDataList = new ArrayList<IData>();
        List<OrderItem> itemsNotPickedUp = new ArrayList<OrderItem>(pickupItems);
        List<FullfillmentPickupItem> fulFilledItems = new ArrayList<FullfillmentPickupItem>();
        if (pickupItems.size() > 0) {
            int pickUpCount = 0;
            if (mOrder.getPickups() != null && mOrder.getPickups().size() > 0) {
                for (Pickup pickup : mOrder.getPickups()) {
                    pickUpCount++;
                    for (PickupItem pickupItem : pickup.getItems()) {
                        itemsNotPickedUp = removeOrderItem(itemsNotPickedUp, pickupItem.getProductCode());
                    }
                    FullfillmentPickupItem item = new FullfillmentPickupItem(pickup, pickUpCount);
                    fulFilledItems.add(item);
                }
            }


            FullfillmentTitleDataItem fullfillmentTitleDataItem = new FullfillmentTitleDataItem();
            fullfillmentTitleDataItem.setTitle("In-Store PickUp Items");
            fullfillmentTitleDataItem.setFullfilledCount(fulFilledItems.size());
            fullfillmentTitleDataItem.setPendingCount(pickupItems.size() - fulFilledItems.size());
            fullfillmentTitleDataItem.setTotalCount(pickupItems.size());
            finalDataList.add(fullfillmentTitleDataItem);
            finalDataList.add(new TopRowItem());
            if (itemsNotPickedUp.size() > 0) {
                finalDataList.add(new FullfillmentHeaderDataItem("Pending Items"));
                for (OrderItem item : itemsNotPickedUp) {
                    FullfillmentDataItem dataItem = new FullfillmentDataItem(item);
                    finalDataList.add(dataItem);
                }
            }
            if (fulFilledItems.size() > 0) {
                finalDataList.add(new FullfillmentHeaderDataItem("PickedUp Items"));
                for (FullfillmentPickupItem fullfilleditem : fulFilledItems) {
                    finalDataList.add(fullfilleditem);
                }
            }


            finalDataList.add(new BottomRowItem());
        }

        return finalDataList;
    }

    private List<IData> filterShipment(List<OrderItem> shipItems) {

        List<IData> finalDataList = new ArrayList<IData>();
        List<FullfillmentPackageDataItem> pendingItems = new ArrayList<FullfillmentPackageDataItem>();
        List<FullfillmentPackageDataItem> fullfilledItems = new ArrayList<FullfillmentPackageDataItem>();
        List<OrderItem> orderItemsNotPackaged = new ArrayList<OrderItem>(shipItems);
        mOrder.getPickups();

        if (shipItems.size() > 0) {
            int packageCount = 0;
            for (Package orderPackage : mOrder.getPackages()) {
                for (PackageItem item : orderPackage.getItems()) {
                    orderItemsNotPackaged = removeOrderItem(orderItemsNotPackaged, item.getProductCode());
                }
                packageCount++;
                FulfillmentItem fulfillmentItem = new FulfillmentItem();
                fulfillmentItem.setPackaged(true);
                fulfillmentItem.setOrderPackage(orderPackage);
                String status = orderPackage.getStatus();

                if (status.equalsIgnoreCase(NOTFULLFILLED)) {
                    fulfillmentItem.setFullfilled(false);
                    fulfillmentItem.setPackageNumber(getActivity().getString(R.string.package_number_string) + String.valueOf(packageCount));
                    pendingItems.add(new FullfillmentPackageDataItem(fulfillmentItem));
                } else if (status.equalsIgnoreCase(FULFILLED)) {
                    fulfillmentItem.setFullfilled(true);
                    for (Shipment shipment : mOrder.getShipments()) {
                        if (shipment.getId().equalsIgnoreCase(orderPackage.getShipmentId())) {
                            fulfillmentItem.setShipment(shipment);
                            break;
                        }
                    }
                    fulfillmentItem.setPackageNumber(getActivity().getString(R.string.package_number_string) + String.valueOf(packageCount));
                    fullfilledItems.add(new FullfillmentPackageDataItem(fulfillmentItem));
                }
            }


            FullfillmentTitleDataItem fullfillmentTitleDataItem = new FullfillmentTitleDataItem();
            fullfillmentTitleDataItem.setTitle("Direct Ship Items");
            fullfillmentTitleDataItem.setFullfilledCount(fullfilledItems.size());
            fullfillmentTitleDataItem.setPendingCount(shipItems.size() - fullfilledItems.size());
            fullfillmentTitleDataItem.setTotalCount(shipItems.size());

            finalDataList.add(fullfillmentTitleDataItem);
            finalDataList.add(new TopRowItem());
            if (orderItemsNotPackaged.size() > 0) {
                finalDataList.add(new FullfillmentHeaderDataItem("Pending Items"));
            }
            for (OrderItem orderItem : orderItemsNotPackaged) {
                finalDataList.add(new FullfillmentDataItem(orderItem));
            }
            if (pendingItems.size() > 0) {
                finalDataList.add(new FullfillmentHeaderDataItem("Created Items"));
            }
            finalDataList.addAll(pendingItems);
            if (fullfilledItems.size() > 0) {
                finalDataList.add(new FullfillmentHeaderDataItem("Shipped Items"));
            }
            finalDataList.addAll(fullfilledItems);
            finalDataList.add(new BottomRowItem());
        }
        return finalDataList;
    }

    private AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            OrderDetailFullfillmentAdapter.RowType rowType = mOrderDetailFullfillmentAdapter.getRowType(position);
            if (rowType == OrderDetailFullfillmentAdapter.RowType.PACKAGE_ROW) {
                FullfillmentPackageDataItem dataItem = (FullfillmentPackageDataItem) mOrderDetailFullfillmentAdapter.getItem(position);
                FragmentManager manager = getFragmentManager();
                PackageInfoDialogFragment packageInfoDialogFragment = (PackageInfoDialogFragment) manager.findFragmentByTag(PACKAGE_DIALOG_TAG);
                UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
                if (packageInfoDialogFragment == null) {
                    packageInfoDialogFragment = new PackageInfoDialogFragment();
                    packageInfoDialogFragment.setFulfillmentItem(dataItem.getFulfillmentItem());
                    packageInfoDialogFragment.setTenantAndSiteId(userState.getTenantId(), userState.getSiteId());
                }
                packageInfoDialogFragment.show(manager, PACKAGE_DIALOG_TAG);
            }else if(rowType == OrderDetailFullfillmentAdapter.RowType.ITEM_ROW){
                FullfillmentDataItem item = (FullfillmentDataItem) mOrderDetailFullfillmentAdapter.getItem(position);
                FragmentManager manager = getFragmentManager();
                ProductDetailOverviewDialogFragment productOverviewFragment = (ProductDetailOverviewDialogFragment) manager.findFragmentByTag(PRODUCT_DIALOG_TAG);
                UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
                if (productOverviewFragment == null) {
                    productOverviewFragment = new ProductDetailOverviewDialogFragment();
                    productOverviewFragment.setProduct(item.getOrderItem().getProduct());
                    productOverviewFragment.setTenantId(userState.getTenantId());
                    productOverviewFragment.setSiteId(userState.getSiteId());
                }
                productOverviewFragment.show(manager, PRODUCT_DIALOG_TAG);

            }else if(rowType == OrderDetailFullfillmentAdapter.RowType.PICKUP_ITEM_ROW){
                FullfillmentPickupItem item = (FullfillmentPickupItem) mOrderDetailFullfillmentAdapter.getItem(position);
                FragmentManager manager = getFragmentManager();
                PickupInfoDialogFragment pickupInfoDialogFragment = (PickupInfoDialogFragment) manager.findFragmentByTag(PICKUP_DIALOG_TAG);
                UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());

                if (pickupInfoDialogFragment == null) {
                    pickupInfoDialogFragment = new PickupInfoDialogFragment();
                    pickupInfoDialogFragment.setPickup(item.getPickup());
                    pickupInfoDialogFragment.setTenantAndSiteId(userState.getTenantId(), userState.getSiteId());
                }

                pickupInfoDialogFragment.show(manager, PICKUP_DIALOG_TAG);
            }

        }

    };

/*
    private void setOrderToViews() {
        setShipmentLayouts();
        setPickupsLayout();
    }

    private void setPickupsLayout() {
        //if no pickups, then hide pickups and move along
        if (mPickupItems.size() < 1) {
            mPickupLabels.setVisibility(View.GONE);
            mPickupDetailLayout.setVisibility(View.GONE);
            ButterKnife.apply(mNotPickedupViews, GONE);
            ButterKnife.apply(mPickedupViews, GONE);
            return;
        }

        boolean isNotPickedupShowing;

        List<Pickup> unPickedUps = new ArrayList<Pickup>();
        List<Pickup> pickedUps = new ArrayList<Pickup>();

        mPickupTotal.setText(String.valueOf(mPickupItems.size()));
        //TODO: Figure out a way to filter on status

        for (Pickup pickup: mPickupItems) {
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
    */
}
