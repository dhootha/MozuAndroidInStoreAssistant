package com.mozu.mozuandroidinstoreassistant.app.data.order;

import android.text.TextUtils;

import com.mozu.api.contracts.commerceruntime.payments.BillingInfo;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

import java.text.NumberFormat;

public class OrderRefundDataItem implements IData {
    private Payment mPayment;

    public OrderRefundDataItem(Payment payment) {
        mPayment = payment;
    }

    public String getPaymentType() {
        if (mPayment.getPaymentType() != null) {
            return mPayment.getPaymentType();
        } else {
            return "N/A";
        }
    }

    public String getAmount() {
        if (mPayment.getAmountCredited() != null) {
            return NumberFormat.getCurrencyInstance().format(mPayment.getAmountCredited());
        } else {
            return "N/A";
        }
    }

    public String getCreditCardNumber() {
        BillingInfo billingInfo = mPayment.getBillingInfo();
        if (billingInfo.getPaymentType().equalsIgnoreCase("CreditCard")) {
            if (!TextUtils.isEmpty(billingInfo.getCard().getCardNumberPartOrMask())) {
                return billingInfo.getCard().getCardNumberPartOrMask();
            }

            return "N/A";
        } else {
            return "N/A";
        }
    }

    public String getCreditCardType() {
        BillingInfo billingInfo = mPayment.getBillingInfo();
        if (billingInfo.getPaymentType().equalsIgnoreCase("CreditCard")) {
            if (!TextUtils.isEmpty(billingInfo.getCard().getPaymentOrCardType())) {
                return billingInfo.getCard().getPaymentOrCardType();
            }

            return "N/A";
        } else {
            return "N/A";
        }

    }


}
