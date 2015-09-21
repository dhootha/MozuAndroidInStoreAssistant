package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.fulfillment.FulfillmentAction;
import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.fulfillment.PackageItem;
import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.api.contracts.commerceruntime.fulfillment.Shipment;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.commerceruntime.payments.PaymentAction;
import com.mozu.api.resources.commerce.orders.PaymentResource;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.BottomRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentColumnHeader;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentDividerRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentFulfilledDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentMoveToDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentPackageDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentPickupItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentCategoryHeaderDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.PickupFulfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShipmentFulfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.TopRowItem;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.FulfillmentMoveToRow.MoveToListener;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.FulfillmentPickupItemRow.MarkPickupAsFulfilledListener;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailFullfillmentAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.FulfillmentActionObservablesManager;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.PickupObservablesManager;
import com.mozu.mozuandroidinstoreassistant.app.product.ProductDetailOverviewDialogFragment;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;


public class OrderDetailFulfillmentFragment extends Fragment implements MoveToListener, MarkPickupAsFulfilledListener {

    private static final String PRODUCT_DIALOG_TAG = "prod_detail_fragment";
    private static final String PACKAGE_DIALOG_TAG = "package_detail_fragment";
    private static final String PICKUP_DIALOG_TAG = "pickup_detail_fragment";
    private List<OrderItem> mShipItems;
    private List<OrderItem> mPickupItems;
    private Order mOrder;
    private ListView mFullfillmentListview;
    private TextView mFulfillmentStatus;

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
            } else if (rowType == OrderDetailFullfillmentAdapter.RowType.ITEM_ROW) {
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

            } else if (rowType == OrderDetailFullfillmentAdapter.RowType.PICKUP_ITEM_ROW) {
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
            } else if (rowType == OrderDetailFullfillmentAdapter.RowType.FULFILLED_ROW) {
                FulfillmentFulfilledDataItem item = (FulfillmentFulfilledDataItem) mOrderDetailFullfillmentAdapter.getItem(position);
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
    private Integer mTenantId;
    private Integer mSiteId;
    private LinearLayout mFulFillmentProgress;
    private TextView mFulFillmentProgressStatus;

    public OrderDetailFulfillmentFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_fulfillment_layout, null);
        mShipItems = new ArrayList<>();
        mPickupItems = new ArrayList<>();
        UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mTenantId = userAuthenticationStateMachine.getTenantId();
        mSiteId = userAuthenticationStateMachine.getSiteId();

        mFullfillmentListview = (ListView) view.findViewById(R.id.fullfillment_list);
        mFulfillmentStatus = (TextView) view.findViewById(R.id.order_fulfillment_status);
        mFulFillmentProgress = (LinearLayout) view.findViewById(R.id.fulfillment_loading);
        mFulFillmentProgressStatus = (TextView) view.findViewById(R.id.progress_update);
        String status = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(mOrder.getFulfillmentStatus()), " ");
        mFulfillmentStatus.setText("Status: " + status);
        if (mOrder != null) {
            categorizeOrdersByFulfillmentMethod(mOrder);
            List<IData> data = new ArrayList<>();

            data.addAll(filterShipment(mShipItems));
            data.addAll(filterPickUp(mPickupItems));
            mOrderDetailFullfillmentAdapter = new OrderDetailFullfillmentAdapter(getActivity(), data, this, this);
            mFullfillmentListview.setAdapter(mOrderDetailFullfillmentAdapter);
            mFullfillmentListview.setOnItemClickListener(mListClickListener);
        }
        return view;
    }

    private void categorizeOrdersByFulfillmentMethod(Order order) {
        if (order == null || order.getItems() == null || order.getItems().size() < 1)
            return;
        for (OrderItem item : order.getItems()) {
            if (OrderStrings.SHIP.equalsIgnoreCase(item.getFulfillmentMethod())) {
                mShipItems.add(item);
            } else if (OrderStrings.PICKUP.equalsIgnoreCase(item.getFulfillmentMethod())) {
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

    /**
     * filter all pickup items.
     *
     * @param pickupItems list of
     * @return
     */
    private List<IData> filterPickUp(List<OrderItem> pickupItems) {
        List<IData> finalDataList = new ArrayList<>();
        List<OrderItem> itemsNotPickedUp = new ArrayList<>(pickupItems);
        List<FulfillmentFulfilledDataItem> fulFilledItems = new ArrayList<>();
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

                    if (pickup.getStatus().equalsIgnoreCase(OrderStrings.NOTFULLFILLED)) {
                        FulfillmentPickupItem item = new FulfillmentPickupItem(pickup, pickUpCount);
                        unFulFilledItems.add(item);

                    } else if (pickup.getStatus().equalsIgnoreCase(OrderStrings.FULFILLED)) {
                        FulfillmentFulfilledDataItem item = new FulfillmentFulfilledDataItem(pickup, pickUpCount);
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
                for (FulfillmentFulfilledDataItem fulfilledItem : fulFilledItems) {
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
     * Filter items fulfilled with the method  by fulfilled, pending, and not packaged.
     *
     * @param shipItems Items already filtered by fulfillment method
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

                if (status.equalsIgnoreCase(OrderStrings.NOTFULLFILLED)) {
                    fulfillmentItem.setFullfilled(false);
                    fulfillmentItem.setPackageNumber(getActivity().getString(R.string.package_number_string) + String.valueOf(packageCount));
                    fulfillmentItem.setFulfillmentContact(mOrder.getFulfillmentInfo().getFulfillmentContact());
                    pendingItems.add(new FulfillmentPackageDataItem(fulfillmentItem));
                } else if (status.equalsIgnoreCase(OrderStrings.FULFILLED)) {
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
                finalDataList.add(new FulfillmentColumnHeader());
                finalDataList.add(new FulfillmentDividerRowItem());
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

    @Override
    public void markPickUpAsFulfilled(Pickup pickup) {
        final FulfillmentAction fulfillmentAction = new FulfillmentAction();
        List<String> pickups = new ArrayList<>();
        pickup.getItems();
        pickups.add(pickup.getId());
        fulfillmentAction.setPickupIds(pickups);
        fulfillmentAction.setActionName("PickUp");

        List<OrderItem> pickupOrderItems = new ArrayList<>();
        Double total = 0.00;
        Double taxTotal = 0.00;
        for (PickupItem pickupItem : pickup.getItems()) {
            for (OrderItem orderItem : mOrder.getItems()) {
                String productCode = orderItem.getProduct().getVariationProductCode();
                if (productCode == null)
                    productCode = orderItem.getProduct().getProductCode();
                if (productCode.equals(pickupItem.getProductCode())) {
                    pickupOrderItems.add(orderItem);
                    total = total + orderItem.getDiscountedTotal();
                    taxTotal = taxTotal + orderItem.getItemTaxTotal();
                }
            }
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View confirmView = inflater.inflate(R.layout.fulfillment_payment_confirm, null);
        final EditText captureAmount = (EditText) confirmView.findViewById(R.id.capture_amount);
        final EditText captureTax = (EditText) confirmView.findViewById(R.id.capture_tax);
        final TextView pickupTotal = (TextView) confirmView.findViewById(R.id.pickup_total);

        captureAmount.setText(total.toString());
        captureTax.setText(taxTotal.toString());
        pickupTotal.setText(String.valueOf(total + taxTotal));
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Double subTotal = 0.00;
                Double tax = 0.00;
                if (!TextUtils.isEmpty(captureAmount.getText().toString())) {
                    subTotal = Double.parseDouble(captureAmount.getText().toString());
                }
                if (!TextUtils.isEmpty(captureTax.getText().toString())) {
                    tax = Double.parseDouble(captureTax.getText().toString());
                }
                pickupTotal.setText(String.valueOf(subTotal + tax));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        captureAmount.addTextChangedListener(textWatcher);
        captureTax.addTextChangedListener(textWatcher);

        new AlertDialog.Builder(getActivity())
                .setView(confirmView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Payment payment = getAuthorizedPayment(mOrder);
                        if (payment != null) {
                            Double pickupAmount = Double.parseDouble(pickupTotal.getText().toString());
                            Double balance = mOrder.getTotal() - getTotalPayment(mOrder.getPayments());
                            captureAndAuthorizePayment(pickupAmount, payment, balance - pickupAmount, fulfillmentAction);
                        } else {
                            Toast.makeText(getActivity(), " No Authorizations available", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create().show();



    /*
        AndroidObservable.bindFragment(this, FulfillmentActionObservablesManager.getInstance(
                mOrder.getTenantId(), mOrder.getSiteId())
                .performFulfillmentAction(action, mOrder.getId()))
                .subscribe(getUpdatePickupSubscriber());
                */
    }

    private Double getTotalPayment(List<Payment> payments) {
        Double total = 0.0;
        for (Payment payment : payments) {
            total += payment.getAmountCollected();
        }
        return total;
    }


    private Payment getAuthorizedPayment(Order mOrder) {
        for (Payment payment : mOrder.getPayments()) {
            if (payment.getStatus().equalsIgnoreCase("authorized")) {
                return payment;
            }
        }

        return null;
    }


    private void captureAndAuthorizePayment(Double captureAmount, final Payment mPayment, final Double remainingAmount, final FulfillmentAction fulfillmentAction) {
        final PaymentAction action = new PaymentAction();
        action.setActionName("CapturePayment");
        action.setAmount(captureAmount);
        action.setCurrencyCode("USD");
        action.setExternalTransactionId(mPayment.getExternalTransactionId());
        mFulFillmentProgress.setVisibility(View.VISIBLE);
        AndroidObservable.bindFragment(OrderDetailFulfillmentFragment.this,
                Observable
                        .create(new Observable.OnSubscribe<Order>() {
                            @Override
                            public void call(Subscriber<? super Order> subscriber) {
                                try {
                                    mFulFillmentProgressStatus.setText("Capturing Payment");
                                    PaymentResource paymentResource = new PaymentResource(new MozuApiContext(mTenantId, mSiteId));
                                    Order capturedOrder = paymentResource.performPaymentAction(action, mPayment.getOrderId(), mPayment.getId());
                                    if (remainingAmount > 0) {
                                        PaymentAction paymentAction = new PaymentAction();
                                        paymentAction.setActionName("CreatePayment");
                                        paymentAction.setAmount(remainingAmount);
                                        paymentAction.setCurrencyCode("USD");
                                        paymentAction.setNewBillingInfo(mPayment.getBillingInfo());
                                        Order updatedOrder = paymentResource.createPaymentAction(paymentAction, mOrder.getId());

                                        if (!subscriber.isUnsubscribed()) {
                                            subscriber.onNext(updatedOrder);
                                            subscriber.onCompleted();
                                        }
                                    } else {

                                        if (!subscriber.isUnsubscribed()) {
                                            subscriber.onNext(capturedOrder);
                                            subscriber.onCompleted();
                                        }
                                    }

                                } catch (Exception e) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onError(e);
                                    }
                                }
                            }
                        }).subscribeOn(Schedulers.io()))
                .subscribe(new Subscriber<Order>() {
                    @Override
                    public void onCompleted() {
                        // mFulFillmentProgressStatus.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Failed to Capture payment" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        mFulFillmentProgressStatus.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Order order) {
                        mFulFillmentProgressStatus.setText("Payment Successfully captured. Marking Item as fulfilled");
                        AndroidObservable.bindFragment(OrderDetailFulfillmentFragment.this, FulfillmentActionObservablesManager.getInstance(
                                mOrder.getTenantId(), mOrder.getSiteId())
                                .performFulfillmentAction(fulfillmentAction, mOrder.getId()))
                                .subscribe(getUpdatePickupSubscriber());
                        //((OrderDetailActivity) getActivity()).onRefresh();
                    }
                });


    }


    @Override
    public void cancelPickup(Pickup pickup) {
        AndroidObservable.bindFragment(this, PickupObservablesManager.getInstance(
                mOrder.getTenantId(), mOrder.getSiteId())
                .deletePickup(pickup.getId(), mOrder.getId()))
                .subscribe(getCancelPickupSubscriber());
    }

    private Subscriber<Order> getUpdatePickupSubscriber() {
        return new Subscriber<Order>() {
            @Override
            public void onCompleted() {
                if (mFulFillmentProgress.getVisibility() == View.VISIBLE) {
                    mFulFillmentProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                ErrorMessageAlertDialog
                        .getStandardErrorMessageAlertDialog(getActivity(), getResources().getString(R.string.standard_error))
                        .show();
            }

            @Override
            public void onNext(Order order) {
                ((OrderDetailActivity) getActivity()).onRefresh();
            }
        };
    }

    private Subscriber<Pickup> getCancelPickupSubscriber() {
        return new Subscriber<Pickup>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ErrorMessageAlertDialog
                        .getStandardErrorMessageAlertDialog(getActivity(), getResources().getString(R.string.standard_error))
                        .show();
            }

            @Override
            public void onNext(Pickup pickup) {
                ((OrderDetailActivity) getActivity()).onRefresh();

            }
        };
    }
}
