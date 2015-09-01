package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.ShippingRate;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShippingItemRow;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class NewOrderShippingItemLayout extends LinearLayout implements IRowLayout, IEditMode {

    private OrderUpdateListener mOrderUpdateListener;
    private Integer mTenantId;
    private Integer mSiteId;
    Spinner mSpinner;
    private CompositeSubscription mCompositeSubscription;

    public void setOrderUpdateListener(OrderUpdateListener updateShippingListener) {
        mOrderUpdateListener = updateShippingListener;
    }

    public NewOrderShippingItemLayout(Context context) {
        super(context);
        init();
    }

    public NewOrderShippingItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewOrderShippingItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        UserAuthenticationStateMachine userStateMachine = UserAuthenticationStateMachineProducer.getInstance(getContext());
        mTenantId = userStateMachine.getTenantId();
        mSiteId = userStateMachine.getSiteId();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()){
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void bindData(IData data) {
        if (data instanceof ShippingItemRow) {
            final ShippingItemRow shippingItemRow = (ShippingItemRow) data;
            mSpinner = (Spinner) findViewById(R.id.shipping_spinner);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.shipping_progress);
            ShippingRate shippingRate = null;
            List<ShippingRate> shipments = new ArrayList<>();
            if (shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodCode() != null) {
                shippingRate = new ShippingRate();
                shippingRate.setShippingMethodCode(shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodCode());
                shippingRate.setShippingMethodName(shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodName());
                shipments.add(shippingRate);
            }
            final SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
            spinnerAdapter.setData(shipments);
            mSpinner.setAdapter(spinnerAdapter);
            setSpinnerSelction(spinnerAdapter, shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodCode());
            mSpinner.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    progressBar.setVisibility(VISIBLE);
                    mCompositeSubscription.add(NewOrderManager.getInstance().getOrderShipments(mTenantId, mSiteId, shippingItemRow.mOrder.getId())
                            .subscribe(new Subscriber<List<ShippingRate>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(List<ShippingRate> shippingRates) {
                                    progressBar.setVisibility(GONE);
                                    spinnerAdapter.setData(shippingRates);
                                    spinnerAdapter.notifyDataSetChanged();
                                    setSpinnerSelction(spinnerAdapter, shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodCode());

                                }
                            }));
                    return false;
                }


            });

            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    if (position == 0)
                        return;

                    Order order = shippingItemRow.mOrder;
                    ShippingRate shippingRateSelected = (ShippingRate) adapterView.getItemAtPosition(position);
                    com.mozu.api.contracts.commerceruntime.fulfillment.FulfillmentInfo fulfillmentInfo = order.getFulfillmentInfo();
                    if (fulfillmentInfo == null) {
                        fulfillmentInfo = new com.mozu.api.contracts.commerceruntime.fulfillment.FulfillmentInfo();
                    }
                    if (shippingRateSelected == null || shippingRateSelected.getShippingMethodCode().equals(shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodCode()))
                        return;
                    fulfillmentInfo.setShippingMethodCode(shippingRateSelected.getShippingMethodCode());
                    fulfillmentInfo.setShippingMethodName(shippingRateSelected.getShippingMethodName());

                    order.setFulfillmentInfo(fulfillmentInfo);
                    progressBar.setVisibility(View.VISIBLE);
                    mCompositeSubscription.add(NewOrderManager.getInstance().getUpdateOrderObservable(mTenantId, mSiteId, order, order.getId())
                            .subscribe(new Subscriber<Order>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.GONE);

                                }

                                @Override
                                public void onNext(Order order) {
                                    progressBar.setVisibility(View.GONE);
                                    mOrderUpdateListener.updateOrder(order);

                                }
                            }));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }
    }

    public void setSpinnerSelction(SpinnerAdapter spinnerAdapter, String spinnerSelection) {
        if (spinnerAdapter.getCount() > 1) {
            for (int i = 1; i < spinnerAdapter.getCount(); i++) {
                String shippingMethod = spinnerAdapter.getItem(i).getShippingMethodCode();
                if (shippingMethod.equals(spinnerSelection)) {
                    mSpinner.setSelection(i);
                    return;
                }
            }
        }
        mSpinner.setSelection(0);
    }


    @Override
    public void setEditMode(boolean isEditMode) {
        mSpinner.setClickable(isEditMode);
        mSpinner.setEnabled(isEditMode);
    }

    public interface OrderUpdateListener {
        public void updateOrder(Order order);
    }


    class SpinnerAdapter extends BaseAdapter {

        private List<ShippingRate> mData;

        public SpinnerAdapter() {
            mData = new ArrayList<>();
        }

        public void setData(List<ShippingRate> data) {
            mData = new ArrayList<>();
            mData.add(null);
            mData.addAll(data);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.orderfulfillment_spinner_item, parent, false);
            }
            TextView mTextView = (TextView) convertView.findViewById(R.id.order_fulfillment);
            ShippingRate shipment = getItem(position);
            if (shipment != null) {
                String shipmentDisplay = shipment.getShippingMethodName() + " (" + shipment.getPrice() + ")";
                mTextView.setText(shipmentDisplay);
            } else {
                mTextView.setText(getResources().getString(R.string.shipping_method));
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public ShippingRate getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(parent.getContext(), R.layout.orderfulfillment_dropdown_resource, null);
            ShippingRate shipment = getItem(position);
            if (shipment != null) {
                String shipmentDisplay = shipment.getShippingMethodName();
                textView.setText(shipmentDisplay);
            } else {
                textView.setText(getResources().getString(R.string.shipping_method));
            }
            return textView;

        }

    }
}
