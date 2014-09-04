package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.*;
import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailDirectShipFulfillmentAdapter;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailFullfillmentFragment extends Fragment {

    public static final String PENDING = "Pending";
    public static final String FULFILLED = "Fulfilled";
    private static final String PRODUCT_DIALOG_TAG = "prod_detail_fragment";
    private static final String PACKAGE_DIALOG_TAG = "package_detail_fragment";
    private Order mOrder;

    private TextView mPendingTotal;
    private TextView mFulfilledTotal;
    private TextView mShipmentTotal;

    private ListView mPackageListView;

    public OrderDetailFullfillmentFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_fulfillment_fragment, null);

        mPendingTotal = (TextView) view.findViewById(R.id.shipment_pending_total);
        mFulfilledTotal = (TextView) view.findViewById(R.id.shipment_fulfilled_total);
        mShipmentTotal = (TextView) view.findViewById(R.id.shipment_total);

        mPackageListView = (ListView) view.findViewById(R.id.shipment_list);

        if (mOrder != null) {
            setOrderToViews(view);
        }

        return view;
    }

    private void setOrderToViews(View view) {

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

        for (OrderItem orderItem: orderItemsNotPackaged) {
            FulfillmentItem fulfillmentItem = new FulfillmentItem();
            fulfillmentItem.setPackaged(false);
            fulfillmentItem.setFullfilled(false);
            fulfillmentItem.setNonPackgedItem(orderItem);

            fulfillmentItemList.add(0, fulfillmentItem);
        }

        mPendingTotal.setText(String.valueOf(pendingCount));
        mFulfilledTotal.setText(String.valueOf(fulfilledCount));
        mShipmentTotal.setText(String.valueOf(pendingCount + fulfilledCount));

        mPackageListView.setAdapter(new OrderDetailDirectShipFulfillmentAdapter(getActivity(), fulfillmentItemList));
        mPackageListView.setOnItemClickListener(mDirectShipClickListener);
    }


    public void setOrder(Order order) {

        mOrder = order;
    }

    private AdapterView.OnItemClickListener mDirectShipClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            FulfillmentItem item = (FulfillmentItem) parent.getItemAtPosition(position);

            if (!item.isPackaged()) {

                showProductDetailDialog(item.getNonPackgedItem());
            } else {

                showPackageDetail(item);
            }

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
            productOverviewFragment.setSiteDomain(userState.getSiteDomain());
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
}
