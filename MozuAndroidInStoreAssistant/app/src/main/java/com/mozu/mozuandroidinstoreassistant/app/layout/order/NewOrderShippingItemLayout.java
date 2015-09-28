package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class NewOrderShippingItemLayout extends LinearLayout implements IRowLayout, IEditMode {

    Spinner mSpinner;
    private OrderUpdateListener mOrderUpdateListener;
    private Integer mTenantId;
    private Integer mSiteId;
    private CompositeSubscription mCompositeSubscription;
    ShippingItemRow shippingItemRow;
    ProgressBar progressBar;

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

    public void setOrderUpdateListener(OrderUpdateListener updateShippingListener) {
        mOrderUpdateListener = updateShippingListener;
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
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }


    private void showAlertDialog(final List<ShippingRate> shippingRates, final String mOrderId) {
        String[] shipList = new String[shippingRates.size()];
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        for (int i = 0; i < shippingRates.size(); i++) {
            ShippingRate shipment = shippingRates.get(i);
            if (shipment != null) {
                StringBuffer shipmentDisplay = new StringBuffer(shipment.getShippingMethodName());
                if (shipment.getPrice() != null) {
                    shipmentDisplay.append(" &nbsp&nbsp&nbsp <font color='#df3018'>" + numberFormat.format(shipment.getPrice()) + " </font> ");
                }
                shipList[i] = Html.fromHtml(shipmentDisplay.toString()).toString();
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        SpinnerDialogAdapter spinnerAdapter = new SpinnerDialogAdapter();
        spinnerAdapter.setData(shippingRates);
        builder.setAdapter(spinnerAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                applyShipping(shippingRates.get(position));
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void applyShipping(ShippingRate shippingRateSelected) {
        Order order = shippingItemRow.mOrder;
        com.mozu.api.contracts.commerceruntime.fulfillment.FulfillmentInfo fulfillmentInfo = order.getFulfillmentInfo();
        if (fulfillmentInfo == null) {
            fulfillmentInfo = new com.mozu.api.contracts.commerceruntime.fulfillment.FulfillmentInfo();
        }
        fulfillmentInfo.setShippingMethodCode(shippingRateSelected.getShippingMethodCode());
        fulfillmentInfo.setShippingMethodName(shippingRateSelected.getShippingMethodName());

        order.setFulfillmentInfo(fulfillmentInfo);
        progressBar.setVisibility(View.VISIBLE);
        mCompositeSubscription.add(NewOrderManager.getInstance().getUpdateOrderObservable(mTenantId, mSiteId, order, order.getId())
                .subscribe(new Subscriber<Order>() {
                    @Override
                    public void onCompleted() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getContext(), "Failed to update shipping");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Order order) {
                        mOrderUpdateListener.updateOrder(order);
                    }
                }));
    }


    @Override
    public void bindData(IData data) {
        if (data instanceof ShippingItemRow) {
            shippingItemRow = (ShippingItemRow) data;
            progressBar = (ProgressBar) findViewById(R.id.shipping_progress);
            mSpinner = (Spinner) findViewById(R.id.shipping_spinner);
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
            List<ShippingRate> shipments = new ArrayList<>();
            if (shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodCode() != null) {
                ShippingRate shippingRate = new ShippingRate();
                shippingRate.setShippingMethodCode(shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodCode());
                shippingRate.setShippingMethodName(shippingItemRow.mCurrentFulfillmentInfo.getShippingMethodName());
                shipments.add(shippingRate);
            } else {
                shipments.add(0, null);
            }
            spinnerAdapter.setData(shipments);
            mSpinner.setAdapter(spinnerAdapter);
            mSpinner.post(new Runnable() {
                @Override
                public void run() {
                    mSpinner.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                                progressBar.setVisibility(VISIBLE);
                                mCompositeSubscription.add(NewOrderManager.getInstance().getOrderShipments(mTenantId, mSiteId, shippingItemRow.mOrder.getId())
                                        .subscribe(new Subscriber<List<ShippingRate>>() {
                                            @Override
                                            public void onCompleted() {
                                                progressBar.setVisibility(GONE);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getContext(), "Failed to fetch Shipping information").show();
                                                progressBar.setVisibility(GONE);
                                            }

                                            @Override
                                            public void onNext(List<ShippingRate> shippingRates) {
                                                showAlertDialog(shippingRates, shippingItemRow.mOrder.getId());
                                            }
                                        }));
                            }
                            return false;
                        }
                    });

                }
            });

        }
    }


    public interface OrderUpdateListener {
        public void updateOrder(Order order);
    }

    @Override
    public void setEditMode(boolean isEditMode) {
    }


    class SpinnerAdapter extends BaseAdapter {

        private List<ShippingRate> mData;

        public SpinnerAdapter() {
            mData = new ArrayList<>();
        }

        public void setData(List<ShippingRate> data) {
            mData = new ArrayList<>();
            mData.addAll(data);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.orderfulfillment_spinner_item, parent, false);
            }
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
            TextView mTextView = (TextView) convertView.findViewById(R.id.order_fulfillment);
            ShippingRate shipment = getItem(position);
            if (shipment != null) {
                StringBuffer shipmentDisplay = new StringBuffer(shipment.getShippingMethodName());
                if (shipment.getPrice() != null) {
                    shipmentDisplay.append(" &nbsp&nbsp&nbsp <font color='#df3018'>" + numberFormat.format(shipment.getPrice()) + " </font> ");
                }
                mTextView.setText(Html.fromHtml(shipmentDisplay.toString()));
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


    class SpinnerDialogAdapter extends BaseAdapter {

        private List<ShippingRate> mData;

        public SpinnerDialogAdapter() {
            mData = new ArrayList<>();
        }

        public void setData(List<ShippingRate> data) {
            mData = new ArrayList<>();
            mData.addAll(data);
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
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.orderfulfillment_spinner_item, parent, false);
            }
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
            TextView mTextView = (TextView) convertView.findViewById(R.id.order_fulfillment);
            ShippingRate shipment = getItem(position);
            if (shipment != null) {
                StringBuffer shipmentDisplay = new StringBuffer(shipment.getShippingMethodName());
                if (shipment.getPrice() != null) {
                    shipmentDisplay.append(" &nbsp&nbsp&nbsp <font color='#df3018'>" + numberFormat.format(shipment.getPrice()) + " </font> ");
                }
                mTextView.setText(Html.fromHtml(shipmentDisplay.toString()));
            } else {
                mTextView.setText(getResources().getString(R.string.shipping_method));
            }
            return convertView;
        }

    }
}
