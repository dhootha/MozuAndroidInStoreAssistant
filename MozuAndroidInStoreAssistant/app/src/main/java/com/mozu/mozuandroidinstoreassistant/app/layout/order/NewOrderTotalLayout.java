package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.commerce.Adjustment;
import com.mozu.api.contracts.commerceruntime.discounts.AppliedDiscount;
import com.mozu.api.contracts.commerceruntime.discounts.ShippingDiscount;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderTotalRow;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

import java.text.NumberFormat;

public class NewOrderTotalLayout extends LinearLayout implements IRowLayout {
    TextView mSubTotalText;

    public NewOrderTotalLayout(Context context) {
        super(context);
    }

    public NewOrderTotalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewOrderTotalLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        if (data instanceof OrderTotalRow) {
            OrderTotalRow orderTotalRow = (OrderTotalRow) data;
            mSubTotalText = (TextView) findViewById(R.id.sub_total_id);
            TextView mTax = (TextView) findViewById(R.id.tax_id);
            TextView mTotal = (TextView) findViewById(R.id.order_total_id);
            TextView mShippingTotal = (TextView) findViewById(R.id.shipping_total_id);
            LinearLayout mShippingAdjustment = (LinearLayout) findViewById(R.id.shipping_adjustment);
            LinearLayout mOrderAdjustment = (LinearLayout) findViewById(R.id.order_adjustment);

            Order order = orderTotalRow.mOrder;
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            mSubTotalText.setText(currencyFormat.format(order.getSubtotal()));
            mShippingTotal.setText(currencyFormat.format(order.getShippingSubTotal()));
            mTax.setText(order.getTaxTotal() != null ? currencyFormat.format(order.getTaxTotal()) : "N/A");
            mTotal.setText(currencyFormat.format(order.getTotal()));
            if (order.getShippingDiscounts().size() > 0) {
                mShippingAdjustment.setVisibility(View.VISIBLE);
                for (ShippingDiscount discount : order.getShippingDiscounts()) {
                    View view = getShippingRowItem(discount.getDiscount());
                    mShippingAdjustment.addView(view);
                }
            }
            if (order.getOrderDiscounts().size() > 0) {
                mOrderAdjustment.setVisibility(View.VISIBLE);
                for (AppliedDiscount discount : order.getOrderDiscounts()) {
                    View view = getShippingRowItem(discount);
                    mOrderAdjustment.addView(view);
                }
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


    private View getShippingRowItem(AppliedDiscount discount) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.neworder_total_item, null);
        TextView column_name = (TextView) view.findViewById(R.id.column_name);
        TextView column_value = (TextView) view.findViewById(R.id.column_value);
        column_name.setText(discount.getDiscount().getName() + " (" + discount.getCouponCode() + ")");
        NumberFormat format = NumberFormat.getCurrencyInstance();
        column_value.setText("(" + format.format(discount.getImpact()) + ")");
        return view;
    }
}
