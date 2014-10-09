package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderRefundDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class OrderRefundItemRow extends LinearLayout implements IRowLayout {
    public OrderRefundItemRow(Context context) {
        super(context);
    }

    public OrderRefundItemRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderRefundItemRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView paymentType = (TextView) findViewById(R.id.refund_payment_type_value);
        TextView paymentCard = (TextView) findViewById(R.id.refund_payment_cardnumber_value);
        TextView paymentCardType = (TextView) findViewById(R.id.refund_payment_cardtype_value);
        TextView paymentAmount = (TextView) findViewById(R.id.refund_payment_amount_credited_value);
        if(data instanceof OrderRefundDataItem){
            OrderRefundDataItem orderRefundDataItem = (OrderRefundDataItem)data;
            paymentType.setText(orderRefundDataItem.getPaymentType());
            paymentCard.setText(orderRefundDataItem.getCreditCardNumber());
            paymentCardType.setText(orderRefundDataItem.getCreditCardType());
            paymentAmount.setText(orderRefundDataItem.getAmount());
        }


    }
}


