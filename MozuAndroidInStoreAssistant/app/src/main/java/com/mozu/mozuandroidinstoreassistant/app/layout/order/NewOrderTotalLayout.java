package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.commerce.Adjustment;
import com.mozu.api.contracts.commerceruntime.discounts.AppliedDiscount;
import com.mozu.api.contracts.commerceruntime.discounts.ShippingDiscount;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderTotalRow;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import java.text.NumberFormat;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class NewOrderTotalLayout extends LinearLayout implements IRowLayout, IEditMode {
    TextView mSubTotalText;
    private boolean mIsEditMode;
    private NewOrderShippingItemLayout.OrderUpdateListener mOrderUpdateListener;
    LinearLayout mShippingAdjustment;
    LinearLayout mOrderAdjustment;
    LinearLayout mDiscountAdjustment;
    private CompositeSubscription mCompositeSubscription;


    public NewOrderTotalLayout(Context context) {
        super(context);
        init();
    }

    public NewOrderTotalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewOrderTotalLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void bindData(IData data) {
        if (data instanceof OrderTotalRow) {
            OrderTotalRow orderTotalRow = (OrderTotalRow) data;
            mSubTotalText = (TextView) findViewById(R.id.sub_total_id);
            TextView mTax = (TextView) findViewById(R.id.tax_id);
            TextView mTotal = (TextView) findViewById(R.id.order_total_id);
            TextView mShippingTotal = (TextView) findViewById(R.id.shipping_total_id);
            mShippingAdjustment = (LinearLayout) findViewById(R.id.shipping_adjustment);
            mOrderAdjustment = (LinearLayout) findViewById(R.id.order_adjustment);
            mDiscountAdjustment = (LinearLayout) findViewById(R.id.discount_adjustment);
            Order order = orderTotalRow.mOrder;
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            mSubTotalText.setText(currencyFormat.format(order.getSubtotal()));
            mShippingTotal.setText(currencyFormat.format(order.getShippingSubTotal()));
            mTax.setText(order.getTaxTotal() != null ? currencyFormat.format(order.getTaxTotal()) : "N/A");
            mTotal.setText(currencyFormat.format(order.getTotal()));
            if (order.getShippingDiscounts().size() > 0) {
                mShippingAdjustment.setVisibility(View.VISIBLE);
                for (ShippingDiscount discount : order.getShippingDiscounts()) {
                    View view = getShippingRowItem(order.getId(), discount.getDiscount());
                    mShippingAdjustment.addView(view);
                }
            } else {
                mShippingAdjustment.removeAllViews();
                mShippingAdjustment.setVisibility(View.GONE);
            }
            if (order.getOrderDiscounts().size() > 0) {
                mDiscountAdjustment.setVisibility(View.VISIBLE);
                mDiscountAdjustment.removeAllViews();
                for (AppliedDiscount discount : order.getOrderDiscounts()) {
                    View view = getShippingRowItem(order.getId(), discount);
                    mDiscountAdjustment.addView(view);
                }
            } else {
                mDiscountAdjustment.removeAllViews();
                mDiscountAdjustment.setVisibility(View.GONE);
            }
            if (order.getAdjustment() != null) {
                mOrderAdjustment.setVisibility(VISIBLE);
                Adjustment adjustment = order.getAdjustment();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.neworder_total_item, null);
                TextView column_name = (TextView) view.findViewById(R.id.column_name);
                TextView column_value = (TextView) view.findViewById(R.id.column_value);
                column_name.setText(adjustment.getDescription());
                NumberFormat format = NumberFormat.getCurrencyInstance();
                column_value.setText(format.format(adjustment.getAmount()));
                mOrderAdjustment.addView(view);
            }
        }

    }

    public void setUpdateListener(NewOrderShippingItemLayout.OrderUpdateListener updateCouponListener) {
        mOrderUpdateListener = updateCouponListener;
    }


    private View getShippingRowItem(final String orderId, final AppliedDiscount discount) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.neworder_total_item, null);
        TextView column_name = (TextView) view.findViewById(R.id.column_name);
        TextView column_value = (TextView) view.findViewById(R.id.column_value);
        TextView removeButton = (TextView) view.findViewById(R.id.remove_icon);
        if (discount.getExcluded()) {
            column_name.setPaintFlags(column_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            column_value.setPaintFlags(column_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ((ViewGroup) removeButton.getParent()).removeView(removeButton);
        } else {
            removeButton.setVisibility(View.VISIBLE);
        }
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCoupon(orderId, discount.getCouponCode());
            }
        });
        column_name.setText(discount.getDiscount().getName() + " (" + discount.getCouponCode() + ")");
        NumberFormat format = NumberFormat.getCurrencyInstance();
        column_value.setText("(" + format.format(discount.getImpact()) + ")");
        return view;
    }

    private void removeCoupon(String orderId, String couponCode) {
        UserAuthenticationStateMachine userAuthenticationState = UserAuthenticationStateMachineProducer.getInstance(getContext());
        Integer mTenantId = userAuthenticationState.getTenantId();
        Integer mSiteId = userAuthenticationState.getSiteId();
        mCompositeSubscription.add(NewOrderManager.getInstance().getRemoveCouponObservable(mTenantId, mSiteId, orderId, couponCode).subscribe(new Subscriber<Order>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getContext(), "Failed to remove Coupon.Please try later");
            }

            @Override
            public void onNext(Order order) {
                mOrderUpdateListener.updateOrder(order);
            }
        }));

    }


    @Override
    public void setEditMode(boolean isEditMode) {

        if (mDiscountAdjustment != null) {
            int discountChildCount = mDiscountAdjustment.getChildCount();
            for (int i = 0; i < discountChildCount; i++) {
                View view = mDiscountAdjustment.getChildAt(i);
                View removeView = view.findViewById(R.id.remove_icon);
                if (removeView != null)
                    removeView.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            }
        }

        if (mShippingAdjustment != null) {
            int discountChildCount = mShippingAdjustment.getChildCount();
            for (int i = 0; i < discountChildCount; i++) {
                View view = mShippingAdjustment.getChildAt(i);
                view.findViewById(R.id.remove_icon).setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            }
        }
    }
}
