package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public class OrderDetailPaymentsAdapter extends ArrayAdapter<Payment> {


    private NumberFormat mNumberFormat;

    public OrderDetailPaymentsAdapter(Context context, List<Payment> payments) {
        super(context, R.layout.payment_list_item);
        mNumberFormat = NumberFormat.getCurrencyInstance();
        addAll(payments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.payment_list_item, parent, false);
        }

        Payment payment = getItem(position);

        TextView paymentDate = (TextView) convertView.findViewById(R.id.payment_date);
        TextView paymentTypeOrCreditCard = (TextView) convertView.findViewById(R.id.payment_type_or_credit_number);
        TextView paymentAmount = (TextView) convertView.findViewById(R.id.payment_amount);

        String date = payment.getAuditInfo() != null && payment.getAuditInfo().getCreateDate() != null ? DateFormat.format("MM/dd/yy  hh:mm a", new Date(payment.getAuditInfo().getCreateDate().getMillis())).toString() : "";

        paymentDate.setText(date);
        payment.getPaymentType();

        if (payment.getBillingInfo() != null && payment.getBillingInfo().getPaymentType() != null) {
            if (payment.getBillingInfo().getPaymentType().equalsIgnoreCase("CreditCard")) {
                paymentTypeOrCreditCard.setText(payment.getBillingInfo().getCard().getCardNumberPartOrMask());
            } else {
                paymentTypeOrCreditCard.setText(payment.getBillingInfo().getPaymentType());
            }
        } else {
            paymentTypeOrCreditCard.setText("N/A");
        }
        paymentAmount.setText(mNumberFormat.format(payment.getAmountCollected()));
        return convertView;
    }

}
