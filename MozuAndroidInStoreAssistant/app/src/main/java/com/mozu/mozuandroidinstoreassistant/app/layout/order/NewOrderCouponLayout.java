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
import android.widget.Toast;

import com.mozu.api.contracts.commerceruntime.discounts.InvalidCoupon;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.productadmin.Discount;
import com.mozu.api.contracts.productadmin.DiscountCollection;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.CouponsRowItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewOrderCouponLayout extends LinearLayout implements IRowLayout, IEditMode {


    private NewOrderShippingItemLayout.OrderUpdateListener mOrderUpdateListener;
    private Integer mTenantId;
    private Integer mSiteId;
    Spinner mSpinner;

    public void setUpdateListener(NewOrderShippingItemLayout.OrderUpdateListener updateCouponListener) {
        mOrderUpdateListener = updateCouponListener;
    }

    public NewOrderCouponLayout(Context context) {
        super(context);
        init();
    }

    public NewOrderCouponLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewOrderCouponLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        UserAuthenticationStateMachine userStateMachine = UserAuthenticationStateMachineProducer.getInstance(getContext());
        mTenantId = userStateMachine.getTenantId();
        mSiteId = userStateMachine.getSiteId();
    }

    @Override
    public void setEditMode(boolean isEditMode) {
        mSpinner.setClickable(isEditMode);
        mSpinner.setEnabled(isEditMode);
    }

    @Override
    public void bindData(IData data) {
        if (data instanceof CouponsRowItem) {
            final CouponsRowItem couponsRowItem = (CouponsRowItem) data;
            mSpinner = (Spinner) findViewById(R.id.coupon_spinner);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.coupon_progress);
            final Order order = couponsRowItem.mOrder;
            final SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
            mSpinner.setAdapter(spinnerAdapter);
            mSpinner.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (spinnerAdapter.getCount() <= 1) {
                        progressBar.setVisibility(VISIBLE);
                        NewOrderManager.getInstance().getCoupons(mTenantId, mSiteId).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<DiscountCollection>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        progressBar.setVisibility(GONE);
                                    }

                                    @Override
                                    public void onNext(DiscountCollection discountCollection) {
                                        progressBar.setVisibility(GONE);
                                        List<Discount> discountList = discountCollection.getItems();
                                        List<String> couponCodeList = new ArrayList<String>();
                                        for (Discount discount : discountList) {
                                            if (discount.getConditions().getCouponCode() != null) {
                                                couponCodeList.add(discount.getConditions().getCouponCode());
                                            }
                                        }
                                        spinnerAdapter.setData(couponCodeList);
                                        spinnerAdapter.notifyDataSetChanged();

                                    }
                                });
                    }

                    return false;
                }
            });
            mSpinner.post(new Runnable() {
                @Override
                public void run() {
                    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            if (position == 0)
                                return;
                            final String couponSelected = (String) adapterView.getItemAtPosition(position);
                            progressBar.setVisibility(View.VISIBLE);
                            NewOrderManager.getInstance().getApplyCouponObervable(mTenantId, mSiteId, order.getId(), couponSelected).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
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
                                            if (order.getInvalidCoupons().size() > 0) {
                                                for (InvalidCoupon invalidCoupon : order.getInvalidCoupons()) {
                                                    if (invalidCoupon.getCouponCode().equals(couponSelected)) {
                                                        Toast.makeText(getContext(), invalidCoupon.getReason(), Toast.LENGTH_LONG).show();
                                                        mSpinner.setSelection(0);
                                                        return;
                                                    }
                                                }
                                            }
                                            mOrderUpdateListener.updateOrder(order);
                                        }
                                    });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
            });


        }
    }


    class SpinnerAdapter extends BaseAdapter {
        private List<String> mData;

        public SpinnerAdapter() {
            mData = new ArrayList<>();
            mData.add(getResources().getString(R.string.select_coupons));
        }

        public void setData(List<String> data) {
            mData.clear();
            mData.add(getResources().getString(R.string.select_coupons));
            mData.addAll(data);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.orderfulfillment_spinner_item, parent, false);
            }
            TextView mTextView = (TextView) convertView.findViewById(R.id.order_fulfillment);
            String couponCode = getItem(position);
            mTextView.setText(couponCode);
            return convertView;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(parent.getContext(), R.layout.orderfulfillment_dropdown_resource, null);
            textView.setText(getResources().getString(R.string.select_coupons));
            return textView;
        }

    }
}
