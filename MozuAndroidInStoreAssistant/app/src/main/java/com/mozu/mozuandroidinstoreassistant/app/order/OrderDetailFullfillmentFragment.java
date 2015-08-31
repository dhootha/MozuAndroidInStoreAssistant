package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.fulfillment.PackageItem;
import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.api.contracts.commerceruntime.fulfillment.Shipment;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.BottomRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentColumnHeader;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentDividerRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentMoveToDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentPackageDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentPickupItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentCategoryHeaderDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.PickupFulfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShipmentFulfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.TopRowItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.FulfillmentMoveToRow;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.product.ProductDetailOverviewDialogFragment;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailFullfillmentAdapter;
import com.mozu.mozuandroidinstoreassistant.app.product.ProductDetailOverviewDialogFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class OrderDetailFullfillmentFragment extends Fragment implements FulfillmentMoveToRow.MoveToListener {

    public static final String PENDING = "Pending";
    public static final String FULFILLED = "Fulfilled";
    public static final String NOTFULLFILLED = "NotFulfilled";

    public static final String SHIP = "Ship";
    public static final String PICKUP = "Pickup";
    public static final String DIGITAL = "digital";

    private static final String PRODUCT_DIALOG_TAG = "prod_detail_fragment";
    private static final String PACKAGE_DIALOG_TAG = "package_detail_fragment";
    private static final String PICKUP_DIALOG_TAG = "pickup_detail_fragment";
    List<OrderItem> mShipItems;
    List<OrderItem> mPickupItems;
    private Order mOrder;
    private ListView mFullfillmentListview;
    private OrderDetailFullfillmentAdapter mOrderDetailFullfillmentAdapter;
    private AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            OrderDetailFullfillmentAdapter.RowType rowType = mOrderDetailFullfillmentAdapter.getRowType(position);
            if (rowType == OrderDetailFullfillmentAdapter.RowType.PACKAGE_ROW) {
                FulfillmentPackageDataItem dataItem = (FulfillmentPackageDataItem) mOrderDetailFullfillmentAdapter.getItem(position);
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
                FulfillmentDataItem item = (FulfillmentDataItem) mOrderDetailFullfillmentAdapter.getItem(position);
                FragmentManager manager = getFragmentManager();
                ProductDetailOverviewDialogFragment productOverviewFragment = (ProductDetailOverviewDialogFragment) manager.findFragmentByTag(PRODUCT_DIALOG_TAG);
                UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
                if (productOverviewFragment == null) {
                    productOverviewFragment = new ProductDetailOverviewDialogFragment();
                    productOverviewFragment.setProduct(item.getOrderItem().getProduct());
                    productOverviewFragment.setTenantId(userState.getTenantId());
                    productOverviewFragment.setSiteId(userState.getSiteId());
                    productOverviewFragment.setSiteDomain(userState.getSiteDomain());

                }
                productOverviewFragment.show(manager, PRODUCT_DIALOG_TAG);

            }else if(rowType == OrderDetailFullfillmentAdapter.RowType.PICKUP_ITEM_ROW){
                FulfillmentPickupItem item = (FulfillmentPickupItem) mOrderDetailFullfillmentAdapter.getItem(position);
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

    public OrderDetailFullfillmentFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_fulfillment_layout, null);
        mShipItems = new ArrayList<>();
        mPickupItems = new ArrayList<>();

        mFullfillmentListview = (ListView) view.findViewById(R.id.fullfillment_list);
        if (mOrder != null) {
            categorizeOrdersByFulfillmentMethod(mOrder);
            List<IData> data = new ArrayList<>();
            data.addAll(filterShipment(mShipItems));
            data.addAll(filterPickUp(mPickupItems));
            mOrderDetailFullfillmentAdapter = new OrderDetailFullfillmentAdapter(getActivity(), data, this);
            mFullfillmentListview.setAdapter(mOrderDetailFullfillmentAdapter);
            mFullfillmentListview.setOnItemClickListener(mListClickListener);
        }
        return view;
    }

    private void categorizeOrdersByFulfillmentMethod(Order order) {
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

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (getActivity() == null) {
            return;
        }
        if (visible) {
            ((OrderDetailActivity) getActivity()).setFulfillmentStatus(mOrder.getFulfillmentStatus());
        } else {
            ((OrderDetailActivity) getActivity()).clearFulfillmentStatus();
        }

    }

    public void setOrder(Order order) {
        mOrder = order;
    }

    /**
     * filter all pickup items.
     *
     * @param pickupItems list of
     * @return
     */
    private List<IData> filterPickUp(List<OrderItem> pickupItems) {
        List<IData> finalDataList = new ArrayList<>();
        List<OrderItem> itemsNotPickedUp = new ArrayList<>(pickupItems);
        List<FulfillmentPickupItem> fulFilledItems = new ArrayList<>();
        List<FulfillmentPickupItem> unFulFilledItems = new ArrayList<>();

        int totalPickupCount = getTotalItemCountFromOrderQuantity(pickupItems);
        int totalFulfilledCount = 0;
        if (pickupItems.size() > 0) {
            int pickUpCount = 0;
            //filter pickup items
            if (mOrder.getPickups() != null && mOrder.getPickups().size() > 0) {
                for (Pickup pickup : mOrder.getPickups()) {
                    int fulfilledCount = 0;
                    pickUpCount++;
                    for (PickupItem pickupItem : pickup.getItems()) {
                        fulfilledCount += pickupItem.getQuantity();
                        itemsNotPickedUp = removeOrderItem(itemsNotPickedUp, pickupItem.getProductCode());
                    }
                    FulfillmentPickupItem item = new FulfillmentPickupItem(pickup, pickUpCount);
                    if (pickup.getStatus().equalsIgnoreCase(NOTFULLFILLED)) {
                        unFulFilledItems.add(item);

                    } else if (pickup.getStatus().equalsIgnoreCase(FULFILLED)) {
                        fulFilledItems.add(item);
                        totalFulfilledCount += fulfilledCount;
                    }
                }
            }

            PickupFulfillmentTitleDataItem fulfillmentTitleDataItem = new PickupFulfillmentTitleDataItem();
            fulfillmentTitleDataItem.setTitle(getActivity().getResources().getString(R.string.instore_header));
            fulfillmentTitleDataItem.setFullfilledCount(totalFulfilledCount);
            fulfillmentTitleDataItem.setUnfullfilledCount(totalPickupCount - totalFulfilledCount);
            fulfillmentTitleDataItem.setTotalCount(totalPickupCount);
            finalDataList.add(fulfillmentTitleDataItem);
            finalDataList.add(new TopRowItem());

            //add not pickedUp items
            if (itemsNotPickedUp.size() > 0) {
                finalDataList.add(new FulfillmentColumnHeader());
                finalDataList.add(new FulfillmentDividerRowItem());
                for (OrderItem item : itemsNotPickedUp) {
                    FulfillmentDataItem dataItem = new FulfillmentDataItem(item);
                    finalDataList.add(dataItem);
                }
                finalDataList.add(new FulfillmentMoveToDataItem(itemsNotPickedUp));
            }

            //add unfulfilled and fulfilled dividers and data to final list.
            if (unFulFilledItems.size() > 0) {
                if (itemsNotPickedUp.size() > 0) {
                    finalDataList.add(new FulfillmentDividerRowItem());
                }
                finalDataList.add(new FullfillmentCategoryHeaderDataItem("Pending Items"));
                for (FulfillmentPickupItem unFulfilledItem : unFulFilledItems) {
                    finalDataList.add(unFulfilledItem);
                }
            }
            if (fulFilledItems.size() > 0) {
                if (unFulFilledItems.size() > 0) {
                    finalDataList.add(new FulfillmentDividerRowItem());
                }
                finalDataList.add(new FullfillmentCategoryHeaderDataItem("PickedUp Items"));
                for (FulfillmentPickupItem fulfilledItem : fulFilledItems) {
                    finalDataList.add(fulfilledItem);
                }
            }

            finalDataList.add(new BottomRowItem());
        }

        return finalDataList;
    }

    private int getTotalItemCountFromOrderQuantity(List<OrderItem> items) {
        int totalItemCount = 0;
        for (OrderItem orderItem : items) {
            totalItemCount += orderItem.getQuantity();
        }
        return totalItemCount;
    }

    /**
     * Filter items fulfilled with the method {@value #SHIP} by fulfilled, pending, and not packaged.
     * @param shipItems Items already filtered by fulfillment method {@value #SHIP}
     * @return filtered list of IData items for
     */
    private List<IData> filterShipment(List<OrderItem> shipItems) {

        int totalItemCount = getTotalItemCountFromOrderQuantity(shipItems);
        int totalFulfilledCount = 0;
        List<IData> finalDataList = new ArrayList<>();
        List<FulfillmentPackageDataItem> pendingItems = new ArrayList<>();
        List<FulfillmentPackageDataItem> fulfilledItems = new ArrayList<>();
        List<OrderItem> orderItemsNotPackaged = new ArrayList<>(shipItems);

        if (shipItems.size() > 0) {
            int packageCount = 0;

            for (Package orderPackage : mOrder.getPackages()) {
                int packageItemCount = 0;
                for (PackageItem item : orderPackage.getItems()) {
                    orderItemsNotPackaged = removeOrderItem(orderItemsNotPackaged, item.getProductCode());
                    packageItemCount += item.getQuantity();
                }
                packageCount++;
                FulfillmentItem fulfillmentItem = new FulfillmentItem();
                fulfillmentItem.setPackaged(true);
                fulfillmentItem.setOrderPackage(orderPackage);
                String status = orderPackage.getStatus();

                if (status.equalsIgnoreCase(NOTFULLFILLED)) {
                    fulfillmentItem.setFullfilled(false);
                    fulfillmentItem.setPackageNumber(getActivity().getString(R.string.package_number_string) + String.valueOf(packageCount));
                    fulfillmentItem.setFulfillmentContact(mOrder.getFulfillmentInfo().getFulfillmentContact());
                    pendingItems.add(new FulfillmentPackageDataItem(fulfillmentItem));
                } else if (status.equalsIgnoreCase(FULFILLED)) {
                    fulfillmentItem.setFullfilled(true);
                    totalFulfilledCount += packageItemCount;
                    for (Shipment shipment : mOrder.getShipments()) {
                        if (shipment.getId().equalsIgnoreCase(orderPackage.getShipmentId())) {
                            fulfillmentItem.setShipment(shipment);
                            break;
                        }
                    }
                    fulfillmentItem.setPackageNumber(getActivity().getString(R.string.package_number_string) + String.valueOf(packageCount));
                    fulfillmentItem.setFulfillmentContact(mOrder.getFulfillmentInfo().getFulfillmentContact());
                    fulfilledItems.add(new FulfillmentPackageDataItem(fulfillmentItem));
                }
            }

            ShipmentFulfillmentTitleDataItem fulfillmentTitleDataItem = new ShipmentFulfillmentTitleDataItem();
            fulfillmentTitleDataItem.setTitle(getString(R.string.direct_ship_header));
            fulfillmentTitleDataItem.setFullfilledCount(totalFulfilledCount);
            fulfillmentTitleDataItem.setUnShippedCount(totalItemCount - totalFulfilledCount);
            fulfillmentTitleDataItem.setTotalCount(totalItemCount);

            finalDataList.add(fulfillmentTitleDataItem);
            finalDataList.add(new TopRowItem());
            if (orderItemsNotPackaged.size() > 0) {
                finalDataList.add(new FullfillmentCategoryHeaderDataItem("Pending Items"));
            }
            for (OrderItem orderItem : orderItemsNotPackaged) {
                finalDataList.add(new FulfillmentDataItem(orderItem));
            }
            if (pendingItems.size() > 0) {
                if (orderItemsNotPackaged.size() > 0) {
                    finalDataList.add(new FulfillmentDividerRowItem());
                }
                finalDataList.add(new FullfillmentCategoryHeaderDataItem("Created Items"));
            }
            finalDataList.addAll(pendingItems);
            if (fulfilledItems.size() > 0) {
                if (pendingItems.size() > 0) {
                    finalDataList.add(new FulfillmentDividerRowItem());
                }
                finalDataList.add(new FullfillmentCategoryHeaderDataItem("Shipped Items"));
            }
            finalDataList.addAll(fulfilledItems);
            finalDataList.add(new BottomRowItem());
        }
        return finalDataList;
    }

    @Override
    public void onMoveToClicked(IData data) {
        if (data instanceof FulfillmentMoveToDataItem) {
            OrderFulfillmentMoveToPickupDialogFragment dialogFragment = new OrderFulfillmentMoveToPickupDialogFragment();
            dialogFragment.setData((FulfillmentMoveToDataItem) data, mOrder);
            dialogFragment.show(getFragmentManager(), "move to");

        }
    }
}
