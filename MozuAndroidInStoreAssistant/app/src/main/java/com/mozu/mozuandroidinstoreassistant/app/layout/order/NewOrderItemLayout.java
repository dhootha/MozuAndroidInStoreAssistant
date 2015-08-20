package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderItemRow;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewOrderItemLayout extends LinearLayout implements IRowLayout {
    @InjectView(R.id.product_code)
    public TextView productCode;

    @InjectView(R.id.product_name)
    public TextView productName;

    @InjectView(R.id.fulfillment_type)
    public TextView productFulfillment;

    @InjectView(R.id.product_quantity)
    public TextView productQuantity;

    @InjectView(R.id.product_price)
    public TextView productPrice;

    @InjectView(R.id.product_total)
    public TextView productTotal;


    public NewOrderItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewOrderItemLayout(Context context) {
        super(context);
    }


    public NewOrderItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {

        if(productTotal == null){
            ButterKnife.inject(this, this);
        }

        if (data instanceof OrderItemRow) {
            OrderItem orderItem = ((OrderItemRow) data).orderItem;
            productCode.setText(orderItem.getProduct().getProductCode());
            productName.setText(orderItem.getProduct().getName());
            productFulfillment.setText(orderItem.getFulfillmentMethod());
            productQuantity.setText(orderItem.getQuantity().toString());
            productPrice.setText(orderItem.getProduct().getPrice().getPrice().toString());
            productTotal.setText(orderItem.getTotal().toString());
        }

    }
}
