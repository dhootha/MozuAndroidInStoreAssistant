package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.product.FulfillmentInfo;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewOrderItemEditFragment extends DialogFragment {


    private static final String ORDER_ITEM = "orderItem";
    private static final String ORDER_ID = "orderId";
    private static final String EDIT_MODE = "editmode";
    private OrderItem mOrderItem;
    private String mOrderId;
    private View mView;

    @InjectView(R.id.product_code)
    public TextView productCode;

    @InjectView(R.id.product_name)
    public TextView productName;

    @InjectView(R.id.product_price)
    public TextView productPrice;
    @InjectView(R.id.product_quantity)
    public EditText productQuantity;
    @InjectView(R.id.product_total)
    public TextView productTotal;

    @InjectView(R.id.product_save)
    public Button productSave;

    private boolean isEditMode = false;


    @InjectView(R.id.fulfillment_type)
    public Spinner fulfillmentType;

    private int mTenantId;
    private int mSiteId;
    private onItemEditDoneListener mEditDoneListener;
    private List<FulfillmentInfo> mFulFillmentInfoList;
    private SpinnerAdapter mSpinnerAdapter;

    public static NewOrderItemEditFragment getInstance(OrderItem orderItem, String orderId, Boolean isEditMode) {
        NewOrderItemEditFragment newOrderItemEditFragment = new NewOrderItemEditFragment();
        Bundle b = new Bundle();
        b.putSerializable(ORDER_ITEM, orderItem);
        b.putString(ORDER_ID, orderId);
        b.putBoolean(EDIT_MODE, isEditMode);
        newOrderItemEditFragment.setArguments(b);
        return newOrderItemEditFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mTenantId = userAuthenticationStateMachine.getTenantId();
        mSiteId = userAuthenticationStateMachine.getSiteId();
        if (getArguments() != null) {
            mOrderId = getArguments().getString(ORDER_ID);
            mOrderItem = (OrderItem) getArguments().getSerializable(ORDER_ITEM);
            isEditMode = getArguments().getBoolean(EDIT_MODE);
        }
        mView = inflater.inflate(R.layout.neworder_item_edit_fragment, null);
        ButterKnife.inject(this, mView);
        if (mOrderItem != null) {
            setupViews();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadLocations();
    }


    private void loadLocations() {
        AndroidObservable.bindFragment(this, NewOrderManager.getInstance().getInstoreLocationCodes(mTenantId, mSiteId, mOrderItem.getProduct().getVariationProductCode()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<FulfillmentInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<FulfillmentInfo> fulfillmentInfos) {
                        mFulFillmentInfoList = fulfillmentInfos;
                        mSpinnerAdapter.setData(fulfillmentInfos);
                        mSpinnerAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void setupViews() {
        Double productPriceVal = mOrderItem.getProduct().getPrice().getPrice();
        productPrice.setText(String.valueOf(productPriceVal));
        productQuantity.setText(String.valueOf(mOrderItem.getQuantity()));
        productCode.setText(mOrderItem.getProduct().getProductCode());
        productName.setText(mOrderItem.getProduct().getName());
        productTotal.setText(String.valueOf(mOrderItem.getQuantity() * productPriceVal));
        mSpinnerAdapter = new SpinnerAdapter();
        fulfillmentType.setAdapter(mSpinnerAdapter);
        productSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditMode) {
                    String updatedProductQuantityVal = productQuantity.getText().toString();
                    FulfillmentInfo updatedFulFillmentType = (FulfillmentInfo) fulfillmentType.getSelectedItem();

                    if (updatedFulFillmentType.mType.equals(mOrderItem.getFulfillmentMethod().toLowerCase()) && updatedFulFillmentType.mLocation.equals(updatedFulFillmentType.mLocation)) {
                        updatedFulFillmentType = null;
                    }
                    if (TextUtils.isEmpty(updatedProductQuantityVal) || updatedProductQuantityVal.equals(String.valueOf(mOrderItem.getQuantity()))) {
                        updatedProductQuantityVal = null;

                    }
                    if (updatedProductQuantityVal != null || updatedFulFillmentType != null) {
                        AndroidObservable.bindFragment(NewOrderItemEditFragment.this, NewOrderManager
                                .getInstance()
                                .getOrderItemUpdateQuantityObservable(mTenantId, mSiteId, mOrderItem, mOrderId, updatedProductQuantityVal == null ? null : Integer.valueOf(updatedProductQuantityVal), updatedFulFillmentType))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Order>() {
                                    @Override
                                    public void onCompleted() {
                                        getDialog().dismiss();

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Order order) {
                                        mEditDoneListener.onEditDone(order);
                                    }
                                });
                    }
                } else {
                    FulfillmentInfo updatedFulFillmentType = (FulfillmentInfo) fulfillmentType.getSelectedItem();
                    mOrderItem.setFulfillmentLocationCode(updatedFulFillmentType.mLocation);
                    mOrderItem.setFulfillmentMethod(updatedFulFillmentType.mType);
                    mOrderItem.setQuantity(Integer.valueOf(productQuantity.getText().toString()));
                    AndroidObservable.bindFragment(NewOrderItemEditFragment.this, NewOrderManager
                            .getInstance()
                            .getOrderItemCreateObservable(mTenantId, mSiteId, mOrderItem, mOrderId))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Order>() {
                                @Override
                                public void onCompleted() {
                                    getDialog().dismiss();

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Order order) {
                                    mEditDoneListener.onEditDone(order);
                                }
                            });

                }

            }
        });
    }

    public void setOnEditDoneListener(onItemEditDoneListener onEditDoneListener) {
        mEditDoneListener = onEditDoneListener;

    }

    class SpinnerAdapter extends BaseAdapter {

        private List<FulfillmentInfo> mData;

        public SpinnerAdapter() {
            mData = new ArrayList<>();
        }

        public void setData(List<FulfillmentInfo> data) {
            mData = data;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.productoption_spinner_item, parent, false);
            }
            TextView mTextView = (TextView) convertView.findViewById(R.id.product_option_name);
            FulfillmentInfo fulfillmentInfo = getItem(position);
            String fulFillmentDisplay = fulfillmentInfo.mType + "_" + fulfillmentInfo.mLocation;
            mTextView.setText(fulFillmentDisplay);
            return convertView;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public FulfillmentInfo getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(parent.getContext(), R.layout.productoption_dropdown_resource, null);
            FulfillmentInfo fulfillmentInfo = getItem(position);
            String fulFillmentDisplay = fulfillmentInfo.mType + "_" + fulfillmentInfo.mLocation;
            textView.setText(fulFillmentDisplay);
            return textView;

        }

    }


    public interface onItemEditDoneListener {
        public void onEditDone(Order order);
    }
}
